package com.mmfsin.betweenminds.data.repository

import android.content.Context
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.domain.interfaces.IOnlineRepository
import com.mmfsin.betweenminds.utils.ROOMS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.random.Random

class OnlineRepository @Inject constructor(
    @ApplicationContext val context: Context,
) : IOnlineRepository {

    private fun aaa(){
        try {
            ProviderInstaller.installIfNeeded(context)
        } catch ( e: GooglePlayServicesRepairableException) {
            // Prompt user to update Play Services
            val a = 2
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Play Services not available
            val a = 2
        }
    }

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

            val roomId = generateRoomCode()
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
            }
                .addOnSuccessListener {
                    roomCodeCreated = roomId
                    latch.countDown()
                    println("Sala creada con código: $roomId")
                }
                .addOnFailureListener { e ->
                    if (attempt == 5) {
                        latch.countDown()
                        return@addOnFailureListener
                    }
                    println("Error creando sala: ${e.message}. Reintentando...")
                    tryCreate(attempt + 1)
                }
        }

        aaa()
        tryCreate(1)
        withContext(Dispatchers.IO) { latch.await() }
        return roomCodeCreated
    }

    private fun generateRoomCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }

    private fun getRandomName(): String {
        val names = context.resources.getStringArray(R.array.random_names)
        return names.random(Random(System.nanoTime()))
    }
}