package com.mmfsin.betweenminds.presentation.packs.detail

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetPackQuestionsUseCase
import com.mmfsin.betweenminds.domain.usecases.GetPackRangesUseCase
import com.mmfsin.betweenminds.domain.usecases.GetPackTypeByIdUseCase
import com.mmfsin.betweenminds.domain.usecases.SelectQuestionsPackUseCase
import com.mmfsin.betweenminds.domain.usecases.SelectRangesPackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailPackViewModel @Inject constructor(
    private val getPackTypeByIdUseCase: GetPackTypeByIdUseCase,
    private val getPackQuestionsUseCase: GetPackQuestionsUseCase,
    private val getPackRangesUseCase: GetPackRangesUseCase,
    private val selectQuestionsPackUseCase: SelectQuestionsPackUseCase,
    private val selectedRangesPackUseCase: SelectRangesPackUseCase,
) : BaseViewModel<DetailPackEvent>() {

    fun getPackTypeById(packId: String) {
        executeUseCase(
            { getPackTypeByIdUseCase.execute(packId) },
            { result ->
                _event.value = if (result == null) DetailPackEvent.SomethingWentWrong
                else DetailPackEvent.GetPack(result)
            },
            { _event.value = DetailPackEvent.SomethingWentWrong }
        )
    }

    fun getQuestions(packNumber: Int) {
        executeUseCase(
            { getPackQuestionsUseCase.execute(packNumber) },
            { result -> _event.value = DetailPackEvent.QuestionsPack(result) },
            { _event.value = DetailPackEvent.SomethingWentWrong }
        )
    }

    fun getRanges(packNumber: Int) {
        executeUseCase(
            { getPackRangesUseCase.execute(packNumber) },
            { result -> _event.value = DetailPackEvent.RangesPack(result) },
            { _event.value = DetailPackEvent.SomethingWentWrong }
        )
    }

    fun selectQuestionPack(packNumber: Int) {
        executeUseCase(
            { selectQuestionsPackUseCase.execute(packNumber) },
            { _event.value = DetailPackEvent.Selected },
            { _event.value = DetailPackEvent.SomethingWentWrong }
        )
    }

    fun selectRangesPack(packNumber: Int) {
        executeUseCase(
            { selectedRangesPackUseCase.execute(packNumber) },
            { _event.value = DetailPackEvent.Selected },
            { _event.value = DetailPackEvent.SomethingWentWrong }
        )
    }
}