package com.mmfsin.betweenminds.presentation.packs.detail

import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.Range

sealed class DetailPackEvent {
    data class QuestionsPack(val data: List<Question>) : DetailPackEvent()
    data class RangesPack(val data: List<Range>) : DetailPackEvent()
    data object SomethingWentWrong : DetailPackEvent()
}