package com.mmfsin.betweenminds.presentation.questions.online.joined

import com.mmfsin.betweenminds.domain.models.OnlineQuestionsAndNames

sealed class OQuestionsJoinedEvent {
    data class GetQuestionsAndNames(val data: OnlineQuestionsAndNames) : OQuestionsJoinedEvent()
    data class OtherPlayerOpinion(val otherOpinion: Int) : OQuestionsJoinedEvent()
    data class GameRestarted(val serverGameNumber: Int) : OQuestionsJoinedEvent()
    data object SomethingWentWrong : OQuestionsJoinedEvent()
}