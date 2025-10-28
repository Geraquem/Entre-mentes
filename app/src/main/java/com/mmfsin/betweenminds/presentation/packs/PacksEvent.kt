package com.mmfsin.betweenminds.presentation.packs

import com.mmfsin.betweenminds.domain.models.QuestionPack

sealed class PacksEvent {
    data class QuestionPacks(val packs: List<QuestionPack>) : PacksEvent()
    data object SomethingWentWrong : PacksEvent()
}