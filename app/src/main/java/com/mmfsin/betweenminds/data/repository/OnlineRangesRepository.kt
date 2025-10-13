package com.mmfsin.betweenminds.data.repository

import android.content.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mmfsin.betweenminds.domain.interfaces.IOnlineRangesRepository
import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.models.OnlineRoundData
import com.mmfsin.betweenminds.utils.PLAYER_1
import com.mmfsin.betweenminds.utils.PLAYER_2
import com.mmfsin.betweenminds.utils.POINTS
import com.mmfsin.betweenminds.utils.ROOMS
import com.mmfsin.betweenminds.utils.ROUNDS
import com.mmfsin.betweenminds.utils.ROUND_DATA
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OnlineRangesRepository @Inject constructor(
    @ApplicationContext val context: Context,
) : IOnlineRangesRepository {

    override suspend fun sendMyORangesDataToRoom(onlineData: OnlineData) {
        val db = Firebase.firestore
        val playerId = if (onlineData.isCreator) PLAYER_1 else PLAYER_2

        val data = onlineData.data.map { round ->
            mapOf(
                "bullseyePosition" to round.bullseyePosition,
                "hint" to round.hint,
                "leftRange" to round.leftRange,
                "rightRange" to round.rightRange
            )
        }

        db.collection(ROOMS).document(onlineData.roomId).collection(playerId).document(ROUNDS)
            .set(mapOf(ROUND_DATA to data)).await()
    }

    override suspend fun waitOtherPlayerORanges(
        roomId: String, isCreator: Boolean
    ): List<OnlineRoundData> = suspendCancellableCoroutine { cont ->
        val db = Firebase.firestore
        val opponentId = if (isCreator) PLAYER_2 else PLAYER_1

        val docRef = db.collection(ROOMS).document(roomId).collection(opponentId).document(ROUNDS)

        var hasResumed = false

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                cont.resumeWithException(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val roundsList = snapshot.get(ROUND_DATA) as? List<Map<String, Any>>
                if (roundsList != null && roundsList.size == 3 && !hasResumed && cont.isActive) {
                    hasResumed = true
                    val parsedRounds = roundsList.mapNotNull { map ->
                        try {
                            OnlineRoundData(
                                round = (map["round"] as? Long)?.toInt() ?: 0,
                                bullseyePosition = (map["bullseyePosition"] as? Long)?.toInt() ?: 0,
                                hint = map["hint"] as? String ?: "",
                                leftRange = map["leftRange"] as? String ?: "",
                                rightRange = map["rightRange"] as? String ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    cont.resume(parsedRounds)
                }
            }
        }

        cont.invokeOnCancellation { listener.remove() }
    }

    override suspend fun sendPoints(
        roomId: String,
        isCreator: Boolean,
        points: Int
    ) {
        val db = Firebase.firestore
        val playerId = if (isCreator) PLAYER_1 else PLAYER_2

        val data = mapOf(POINTS to points)

        db.collection(ROOMS).document(roomId).collection(playerId).document(POINTS).set(data)
            .await()
    }

    override suspend fun waitOtherPlayerPoints(roomId: String, isCreator: Boolean): Int =
        suspendCancellableCoroutine { cont ->
            val db = Firebase.firestore
            val opponentId = if (isCreator) PLAYER_2 else PLAYER_1

            val opponentRef = db.collection(ROOMS)
                .document(roomId)
                .collection(opponentId)
                .document(POINTS)

            val listener = opponentRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    cont.resumeWithException(error)
                    return@addSnapshotListener
                }

                if (snapshot?.exists() == true) {
                    val points = (snapshot.get(POINTS) as? Long)?.toInt()
                    if (points != null && cont.isActive) {
                        cont.resume(points)
                    }
                }
            }

            cont.invokeOnCancellation { listener.remove() }
        }

    override suspend fun restartGame(roomId: String) {
        val db = Firebase.firestore
        val roomRef = db.collection(ROOMS).document(roomId)

        suspend fun clearPlayerData(playerId: String) {
            val playerRef = roomRef.collection(playerId)
            val snapshot = playerRef.get().await()
            for (doc in snapshot.documents) {
                playerRef.document(doc.id).delete().await()
            }
        }
        clearPlayerData(PLAYER_1)
        clearPlayerData(PLAYER_2)
    }

    override suspend fun waitCreatorToRestartGame(roomId: String) =
        suspendCancellableCoroutine { cont ->
            val db = Firebase.firestore
            val roomRef = db.collection(ROOMS).document(roomId).collection(PLAYER_1)

            val listener = roomRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    if (cont.isActive) cont.resumeWith(Result.failure(e))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.isEmpty) {
                    if (cont.isActive) cont.resumeWith(Result.success(Unit))
                }
            }

            cont.invokeOnCancellation { listener.remove() }
        }
}