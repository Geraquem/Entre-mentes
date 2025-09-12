package com.mmfsin.betweenminds.data.repository

import android.content.Context
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mmfsin.betweenminds.data.mappers.toQuestionsList
import com.mmfsin.betweenminds.data.models.QuestionDTO
import com.mmfsin.betweenminds.domain.interfaces.IQuestionsRepository
import com.mmfsin.betweenminds.domain.interfaces.IRealmDatabase
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.utils.QUESTIONS
import com.mmfsin.betweenminds.utils.SERVER_QUESTIONS
import com.mmfsin.betweenminds.utils.SHARED_PREFS
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class QuestionsRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val realmDatabase: IRealmDatabase
) : IQuestionsRepository {

    override suspend fun getQuestions(): List<Question> {
        val latch = CountDownLatch(1)
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

        val questions = mutableListOf<QuestionDTO>()
        if (sharedPrefs.getBoolean(SERVER_QUESTIONS, true)) {
            Firebase.database.reference.child(QUESTIONS).get().addOnSuccessListener {
                for (child in it.children) {
                    val question = QuestionDTO().apply {
                        id = child.key.toString()
                        text = child.value.toString()
                    }
                    CoroutineScope(Dispatchers.IO).launch {   saveQuestionInRealm(question)}
                    questions.add(question)
                }
                sharedPrefs.edit().apply {
                    putBoolean(SERVER_QUESTIONS, false)
                    apply()
                }
                latch.countDown()

            }.addOnFailureListener {
                latch.countDown()
            }

            withContext(Dispatchers.IO) { latch.await() }
            return questions.toQuestionsList()

        } else {
            val saved = realmDatabase.getObjectsFromRealm { query<QuestionDTO>().find() }
            return saved.toQuestionsList()
        }
    }

    private suspend fun saveQuestionInRealm(question: QuestionDTO) {
        realmDatabase.write { question }
    }
}