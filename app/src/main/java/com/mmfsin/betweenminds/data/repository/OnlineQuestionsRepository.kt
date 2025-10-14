package com.mmfsin.betweenminds.data.repository

import android.content.Context
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mmfsin.betweenminds.domain.interfaces.IOnlineQuestionsRepository
import com.mmfsin.betweenminds.domain.models.OnlineQuestionsAndNames
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.utils.PLAYER_1
import com.mmfsin.betweenminds.utils.PLAYER_2
import com.mmfsin.betweenminds.utils.ROOMS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume

class OnlineQuestionsRepository @Inject constructor(
    @ApplicationContext val context: Context,
) : IOnlineQuestionsRepository {

    override suspend fun setQuestionsInRoom(
        roomId: String,
        names: Pair<String, String>,
        questions: List<Question>
    ) {
        val db = Firebase.firestore
        val data = mapOf(
            "blueName" to names.first,
            "orangeName" to names.second,
            "questions" to questions
        )
        db.collection(ROOMS).document(roomId).set(data, SetOptions.merge()).await()
    }

    override suspend fun getQuestionsAndNames(roomId: String): OnlineQuestionsAndNames =
        suspendCancellableCoroutine { cont ->
            val db = Firebase.firestore
            val roomRef = db.collection(ROOMS).document(roomId)

            var listener: ListenerRegistration? = null

            listener = roomRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    if (cont.isActive) cont.resumeWith(Result.failure(error))
                    listener?.remove()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val blueName = snapshot.getString("blueName")
                    val orangeName = snapshot.getString("orangeName")
                    val questionsList = snapshot.get("questions") as? List<Map<String, Any>>

                    if (!blueName.isNullOrEmpty() && !orangeName.isNullOrEmpty() && !questionsList.isNullOrEmpty()) {
                        val parsedQuestions = questionsList.mapNotNull { map ->
                            try {
                                Question(text = map["text"] as? String ?: "")
                            } catch (e: Exception) {
                                null
                            }
                        }

                        val data = OnlineQuestionsAndNames(
                            questions = parsedQuestions,
                            blueName = blueName,
                            orangeName = orangeName
                        )

                        if (cont.isActive) {
                            cont.resume(data)
                            listener?.remove()
                        }
                    }
                }
            }

            cont.invokeOnCancellation { listener?.remove() }
        }

    override suspend fun sendOpinionOQuestionsToRoomUseCase(
        roomId: String,
        isCreator: Boolean,
        round: Int,
        orangeOpinion: Int
    ) {
        val db = Firebase.firestore
        val playerId = if (isCreator) PLAYER_1 else PLAYER_2

        val data = mapOf(
            "round" to round,
            "orangeOpinion" to orangeOpinion,
        )

        db.collection(ROOMS).document(roomId).collection(playerId).document("$round")
            .set(data).await()
    }
}