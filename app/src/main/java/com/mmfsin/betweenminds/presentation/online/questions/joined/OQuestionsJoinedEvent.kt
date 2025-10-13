package com.mmfsin.betweenminds.presentation.online.questions.joined

import com.mmfsin.betweenminds.domain.models.OnlineQuestionsAndNames

sealed class OQuestionsJoinedEvent {
    data class GetQuestionsAndNames(val data: OnlineQuestionsAndNames) : OQuestionsJoinedEvent()
    data object SomethingWentWrong : OQuestionsJoinedEvent()
}