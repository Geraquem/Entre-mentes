package com.mmfsin.betweenminds.data.repository

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mmfsin.betweenminds.domain.interfaces.IOnlineRoomRepository
import com.mmfsin.betweenminds.utils.PLAYERS
import com.mmfsin.betweenminds.utils.PLAYER_1
import com.mmfsin.betweenminds.utils.PLAYER_2
import com.mmfsin.betweenminds.utils.ROOMS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class OnlineRoomRepository @Inject constructor(
    @ApplicationContext val context: Context,
) : IOnlineRoomRepository {

    override suspend fun createRoom(): String? {
        val db = Firebase.firestore
        val latch = CountDownLatch(1)

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
                        "players" to listOf(PLAYER_1),
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
        return (1..4).map { chars.random() }.joinToString("")
    }

    override suspend fun joinRoom(roomId: String): Boolean {
        val db = Firebase.firestore
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
                players.add(PLAYER_2)
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

    override suspend fun restartGameAndResetRoom(roomId: String) {
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
}