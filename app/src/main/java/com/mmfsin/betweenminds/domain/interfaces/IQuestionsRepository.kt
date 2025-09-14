package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.Range

interface IQuestionsRepository {
    suspend fun getRanges(): List<Range>
    suspend fun getQuestions(): List<Question>
}