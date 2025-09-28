package com.mmfsin.betweenminds.presentation.question

import com.mmfsin.betweenminds.domain.models.Question

sealed class QuestionEvent {
    data class Questions(val questions: List<Question>) : QuestionEvent()
    data object SomethingWentWrong : QuestionEvent()
}