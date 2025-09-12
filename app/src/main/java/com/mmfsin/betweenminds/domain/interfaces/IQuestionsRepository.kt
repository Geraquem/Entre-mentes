package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.Question

interface IQuestionsRepository {
    suspend fun getQuestions(): List<Question>
}