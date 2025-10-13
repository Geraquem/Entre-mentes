package com.mmfsin.betweenminds.presentation.online.questions

import com.mmfsin.betweenminds.domain.models.Question

sealed class OQuestionsEvent {
    data class GetQuestions(val questions: List<Question>) : OQuestionsEvent()
    data object QuestionsSetInRoom : OQuestionsEvent()

    //    data class OtherPlayerData(val data: List<OnlineRoundData>) : OQuestionsEvent()
//    data class OtherPlayerPoints(val otherPlayerPoints: Int) : OQuestionsEvent()
//    data object GameRestarted : OQuestionsEvent()
    data object SomethingWentWrong : OQuestionsEvent()
}