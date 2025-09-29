package com.mmfsin.betweenminds.presentation.question

import com.mmfsin.betweenminds.domain.models.Question

sealed class QuestionsEvent {
    data class Questions(val questions: List<Question>) : QuestionsEvent()
    data object SomethingWentWrong : QuestionsEvent()
}