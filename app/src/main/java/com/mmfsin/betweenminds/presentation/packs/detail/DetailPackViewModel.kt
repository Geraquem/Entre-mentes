package com.mmfsin.betweenminds.presentation.packs.detail

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetPackQuestionsUseCase
import com.mmfsin.betweenminds.domain.usecases.GetPackRangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailPackViewModel @Inject constructor(
    private val getPackQuestionsUseCase: GetPackQuestionsUseCase,
    private val getPackRangesUseCase: GetPackRangesUseCase,
) : BaseViewModel<DetailPackEvent>() {

    fun getQuestions(packId: String) {
        executeUseCase(
            { getPackQuestionsUseCase.execute(packId) },
            { result -> _event.value = DetailPackEvent.QuestionsPack(result) },
            { _event.value = DetailPackEvent.SomethingWentWrong }
        )
    }

    fun getRanges(packId: String) {
        executeUseCase(
            { getPackRangesUseCase.execute(packId) },
            { result -> _event.value = DetailPackEvent.RangesPack(result) },
            { _event.value = DetailPackEvent.SomethingWentWrong }
        )
    }
}