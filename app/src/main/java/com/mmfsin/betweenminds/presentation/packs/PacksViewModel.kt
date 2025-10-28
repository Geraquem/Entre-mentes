package com.mmfsin.betweenminds.presentation.packs

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetQuestionsPacksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PacksViewModel @Inject constructor(
    private val getQuestionsPacksUseCase: GetQuestionsPacksUseCase
) : BaseViewModel<PacksEvent>() {

    fun getQuestionsPack() {
        executeUseCase(
            { getQuestionsPacksUseCase.execute() },
            { result -> _event.value = PacksEvent.QuestionPacks(result) },
            { _event.value = PacksEvent.SomethingWentWrong }
        )
    }
}