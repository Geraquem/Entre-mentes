package com.mmfsin.betweenminds.presentation.packs.questions

import com.mmfsin.betweenminds.domain.models.QuestionPack

sealed class QuestionsPacksEvent {
    data class QuestionsPacks(val packs: List<QuestionPack>) : QuestionsPacksEvent()
    data class SelectedPack(val selected: Int) : QuestionsPacksEvent()
    data class NewPackSelected(val packNumber: Int) : QuestionsPacksEvent()
    data object SomethingWentWrong : QuestionsPacksEvent()
}