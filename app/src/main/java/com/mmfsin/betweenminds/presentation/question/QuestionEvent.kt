package com.mmfsin.betweenminds.presentation.question

import com.mmfsin.betweenminds.domain.models.Phrase

sealed class QuestionEvent {
    data class Phrases(val phrases: List<Phrase>) : QuestionEvent()
    data object SomethingWentWrong : QuestionEvent()
}