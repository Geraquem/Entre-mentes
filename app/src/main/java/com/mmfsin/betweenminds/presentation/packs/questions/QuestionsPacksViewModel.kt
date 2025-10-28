package com.mmfsin.betweenminds.presentation.packs.questions

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetQuestionsPacksUseCase
import com.mmfsin.betweenminds.domain.usecases.GetSelectedQuestionsPackUseCase
import com.mmfsin.betweenminds.domain.usecases.SelectQuestionsPackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuestionsPacksViewModel @Inject constructor(
    private val getSelectedQuestionsPackUseCase: GetSelectedQuestionsPackUseCase,
    private val getQuestionsPacksUseCase: GetQuestionsPacksUseCase,
    private val selectedQuestionsPackUseCase: SelectQuestionsPackUseCase
) : BaseViewModel<QuestionsPacksEvent>() {

    fun getSelectedQuestionPack() {
        executeUseCase(
            { getSelectedQuestionsPackUseCase.execute() },
            { result -> _event.value = QuestionsPacksEvent.SelectedPack(result) },
            { _event.value = QuestionsPacksEvent.SomethingWentWrong }
        )
    }

    fun getQuestionsPack() {
        executeUseCase(
            { getQuestionsPacksUseCase.execute() },
            { result -> _event.value = QuestionsPacksEvent.QuestionsPacks(result) },
            { _event.value = QuestionsPacksEvent.SomethingWentWrong }
        )
    }

    fun selectQuestionPack(packId: Int) {
        executeUseCase(
            { selectedQuestionsPackUseCase.execute(packId) },
            { _event.value = QuestionsPacksEvent.NewPackSelected(packId) },
            { _event.value = QuestionsPacksEvent.SomethingWentWrong }
        )
    }
}