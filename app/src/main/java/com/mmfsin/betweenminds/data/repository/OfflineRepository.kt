package com.mmfsin.betweenminds.data.repository

import android.content.Context
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mmfsin.betweenminds.data.mappers.toQuestionsList
import com.mmfsin.betweenminds.data.mappers.toRangesList
import com.mmfsin.betweenminds.data.models.PackDTO
import com.mmfsin.betweenminds.data.models.QuestionDTO
import com.mmfsin.betweenminds.data.models.RangeDTO
import com.mmfsin.betweenminds.domain.interfaces.IOfflineRepository
import com.mmfsin.betweenminds.domain.interfaces.IRealmDatabase
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.utils.SERVER_QUESTIONS
import com.mmfsin.betweenminds.utils.SERVER_RANGES
import com.mmfsin.betweenminds.utils.SHARED_PREFS
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class OfflineRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val realmDatabase: IRealmDatabase
) : IOfflineRepository {

    override suspend fun getRanges(): List<Range> {
        val latch = CountDownLatch(1)
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

        val ranges = mutableListOf<RangeDTO>()
        if (sharedPrefs.getBoolean(SERVER_RANGES, true)) {
            realmDatabase.deleteAllObjects(RangeDTO::class)

//            Firebase.database.reference.child(RANGES).get().addOnSuccessListener {
            Firebase.database.reference.child("ranges_test").get().addOnSuccessListener {
                for (child in it.children) {
                    child.getValue(RangeDTO::class.java)?.let { range ->
                        saveRangesInRealm(range)
                        ranges.add(range)
                    }
                    sharedPrefs.edit().apply {
                        putBoolean(SERVER_RANGES, false)
                        apply()
                    }
                }
                latch.countDown()

            }.addOnFailureListener {
                latch.countDown()
            }

            withContext(Dispatchers.IO) { latch.await() }
            return ranges.toRangesList()

        } else {
            val saved = realmDatabase.getObjectsFromRealm { query<RangeDTO>().find() }
            return saved.toRangesList()
        }
    }

    override suspend fun getQuestions(): List<Question> {
        val latch = CountDownLatch(1)
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

        val questions = mutableListOf<QuestionDTO>()
        if (sharedPrefs.getBoolean(SERVER_QUESTIONS, true)) {
            realmDatabase.deleteAllObjects(QuestionDTO::class)
//            Firebase.database.reference.child(QUESTIONS).get().addOnSuccessListener {
            Firebase.database.reference.child("questions_test").get().addOnSuccessListener {
                for (child in it.children) {
                    child.getValue(QuestionDTO::class.java)?.let { question ->
                        saveQuestionInRealm(question)
                        questions.add(question)
                    }
                    sharedPrefs.edit().apply {
                        putBoolean(SERVER_QUESTIONS, false)
                        apply()
                    }
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

    private fun saveQuestionInRealm(question: QuestionDTO) {
        try {
            realmDatabase.addObject { question }
        } catch (e: Exception) {
            println("Error writing in realm")
        }
    }

    private fun saveRangesInRealm(range: RangeDTO) {
        try {
            realmDatabase.addObject { range }
        } catch (e: Exception) {
            println("Error writing in realm")
        }
    }
}