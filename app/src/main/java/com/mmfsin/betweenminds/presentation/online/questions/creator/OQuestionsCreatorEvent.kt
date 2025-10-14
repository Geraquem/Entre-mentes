package com.mmfsin.betweenminds.presentation.online.questions.creator

import com.mmfsin.betweenminds.domain.models.Question

sealed class OQuestionsCreatorEvent {
    data class GetQuestions(val questions: List<Question>) : OQuestionsCreatorEvent()
    data object QuestionsSetInRoom : OQuestionsCreatorEvent()
    data class OtherPlayerOpinion(val otherOpinion: Int) : OQuestionsCreatorEvent()
    data object GameRestarted : OQuestionsCreatorEvent()
    data object SomethingWentWrong : OQuestionsCreatorEvent()
}