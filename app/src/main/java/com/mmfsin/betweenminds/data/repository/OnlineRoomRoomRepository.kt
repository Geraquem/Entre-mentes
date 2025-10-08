package com.mmfsin.betweenminds.data.repository

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.domain.interfaces.IOnlineRoomRepository
import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.utils.PLAYERS
import com.mmfsin.betweenminds.utils.PLAYER_1
import com.mmfsin.betweenminds.utils.PLAYER_2
import com.mmfsin.betweenminds.utils.ROOMS
import com.mmfsin.betweenminds.utils.ROUNDS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.random.Random

class OnlineRoomRoomRepository @Inject constructor(
    @ApplicationContext val context: Context,
) : IOnlineRoomRepository {

    override suspend fun createRoom(userName: String): String? {
        val db = Firebase.firestore
        val latch = CountDownLatch(1)
        val userId = userName.ifEmpty { getRandomName() }

        var roomCodeCreated: String? = null

        fun tryCreate(attempt: Int) {
            if (attempt > 5) {
                println("No se ha podido crear la sala después de 5 intentos.")
                return
            }

            val roomId = generateRoomId()
            val roomRef = db.collection(ROOMS).document(roomId)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(roomRef)
                if (snapshot.exists()) {
                    throw Exception("Código $roomId ya está en uso")
                }

                transaction.set(
                    roomRef, hashMapOf(
                        "roomId" to roomId,
                        "createdBy" to userId,
                        "players" to listOf(userId),
                        "turn" to userId,
                        "round" to 1,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
            }.addOnSuccessListener {
                roomCodeCreated = roomId
                latch.countDown()
                println("Sala creada con código: $roomId")
            }.addOnFailureListener { e ->
                if (attempt == 5) {
                    latch.countDown()
                    return@addOnFailureListener
                }
                println("Error creando sala: ${e.message}. Reintentando...")
                tryCreate(attempt + 1)
            }
        }

        tryCreate(1)
        withContext(Dispatchers.IO) { latch.await() }
        return roomCodeCreated
    }

    private fun generateRoomId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars.random() }.joinToString("")
    }

    override suspend fun joinRoom(userName: String, roomId: String): Boolean {
        val db = Firebase.firestore
        val userId = userName.ifEmpty { getRandomName() }
        var joined = false
        val latch = CountDownLatch(1)

        val roomRef = db.collection(ROOMS).document(roomId)

        roomRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                latch.countDown()
                return@addOnSuccessListener
            }

            val players =
                (snapshot.get(PLAYERS) as? List<String>)?.toMutableList() ?: mutableListOf()

            if (players.size >= 2) {
                latch.countDown()
                return@addOnSuccessListener
            } else {
                players.add(userId)
                roomRef.update(PLAYERS, players).addOnSuccessListener {
                    joined = true
                    println("Joined succesfully to room: $roomId")
                    latch.countDown()

                }.addOnFailureListener { latch.countDown() }
            }
        }.addOnFailureListener {
            println("Error: ${it.message}")
            latch.countDown()
        }

        withContext(Dispatchers.IO) { latch.await() }
        return joined
    }

    private fun getRandomName(): String {
        val names = context.resources.getStringArray(R.array.random_names)
        return names.random(Random(System.nanoTime()))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun waitToJoinRoom(roomId: String) = suspendCancellableCoroutine { cont ->
        val db = Firebase.firestore
        val roomRef = db.collection(ROOMS).document(roomId)

        val listener = roomRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                if (cont.isActive) cont.resumeWith(Result.failure(e))
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val players = (snapshot.get(PLAYERS) as? List<String>) ?: emptyList()
                if (players.size >= 2) {
                    if (cont.isActive) cont.resumeWith(Result.success(Unit))
                }
            }
        }

        cont.invokeOnCancellation { listener.remove() }
    }

    override suspend fun sendDataToOtherPlayer(data: OnlineData) {
        val db = Firebase.firestore
        val playerData = mapOf(
            "bullseyePosition" to data.bullseyePosition,
            "hint" to data.hint,
            "leftRange" to data.leftRange,
            "rightRange" to data.rightRange
        )

        val playerId = if (data.isCreator) PLAYER_1 else PLAYER_2
        db.collection(ROOMS)
            .document(data.roomId)
            .collection(ROUNDS)
            .document(data.round.toString())
            .set(
                mapOf(playerId to playerData),
                SetOptions.merge()
            ).await()
    }
}