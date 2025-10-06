package com.mmfsin.betweenminds.presentation.online.ranges.joined

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetRangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ORangesJoinedViewModel @Inject constructor(
    private val getRangesUseCase: GetRangesUseCase
) : BaseViewModel<ORangesJoinedEvent>() {

    fun getRanges() {
//        executeUseCase(
//            { getRangesUseCase.execute() },
//            { result -> _event.value = RangesEvent.Ranges(result) },
//            { _event.value = RangesEvent.SomethingWentWrong }
//        )
    }
}