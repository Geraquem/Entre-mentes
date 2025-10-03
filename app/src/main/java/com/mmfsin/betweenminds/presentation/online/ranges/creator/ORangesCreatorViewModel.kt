package com.mmfsin.betweenminds.presentation.online.ranges.creator

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetRangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ORangesCreatorViewModel @Inject constructor(
    private val getRangesUseCase: GetRangesUseCase
) : BaseViewModel<ORangesCreatorEvent>() {

    fun getRanges() {
//        executeUseCase(
//            { getRangesUseCase.execute() },
//            { result -> _event.value = RangesEvent.Ranges(result) },
//            { _event.value = RangesEvent.SomethingWentWrong }
//        )
    }
}