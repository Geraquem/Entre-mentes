package com.mmfsin.betweenminds.presentation.online.questions.creator

import com.mmfsin.betweenminds.domain.models.Question

sealed class OQuestionsCreatorEvent {
    data class GetQuestionsCreator(val questions: List<Question>) : OQuestionsCreatorEvent()
    data object QuestionsCreatorSetInRoom : OQuestionsCreatorEvent()
    data class OtherPlayerOpinion(val otherOpinion: Int) : OQuestionsCreatorEvent()

    //    data class OtherPlayerPoints(val otherPlayerPoints: Int) : OQuestionsEvent()
//    data object GameRestarted : OQuestionsEvent()
    data object SomethingWentWrong : OQuestionsCreatorEvent()
}