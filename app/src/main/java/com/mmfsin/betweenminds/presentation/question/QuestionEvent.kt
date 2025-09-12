package com.mmfsin.betweenminds.presentation.question

import com.mmfsin.betweenminds.domain.models.Question

sealed class QuestionEvent {
    data class Questions(val phrases: List<Question>) : QuestionEvent()
    data object SomethingWentWrong : QuestionEvent()
}