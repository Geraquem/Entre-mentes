package com.mmfsin.betweenminds.presentation.questions.online.creator

import com.mmfsin.betweenminds.domain.models.Question

sealed class OQuestionsCreatorEvent {
    data class GetQuestions(val questions: List<Question>) : OQuestionsCreatorEvent()
    data object QuestionsSetInRoom : OQuestionsCreatorEvent()
    data class OtherPlayerOpinion(val otherOpinion: Float) : OQuestionsCreatorEvent()
    data object GameRestarted : OQuestionsCreatorEvent()
    data object SomethingWentWrong : OQuestionsCreatorEvent()
}