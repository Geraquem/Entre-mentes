package com.mmfsin.betweenminds.presentation.packs.ranges

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetRangesPacksUseCase
import com.mmfsin.betweenminds.domain.usecases.GetSelectedRangesPackUseCase
import com.mmfsin.betweenminds.domain.usecases.SelectRangesPackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RangesPacksViewModel @Inject constructor(
    private val getSelectedRangesPackUseCase: GetSelectedRangesPackUseCase,
    private val getRangesPacksUseCase: GetRangesPacksUseCase,
    private val selectedRangesPackUseCase: SelectRangesPackUseCase
) : BaseViewModel<RangesPacksEvent>() {

    fun getSelectedRangePack() {
        executeUseCase(
            { getSelectedRangesPackUseCase.execute() },
            { result -> _event.value = RangesPacksEvent.SelectedPack(result) },
            { _event.value = RangesPacksEvent.SomethingWentWrong }
        )
    }

    fun getRangesPack() {
        executeUseCase(
            { getRangesPacksUseCase.execute() },
            { result -> _event.value = RangesPacksEvent.RangesPacks(result) },
            { _event.value = RangesPacksEvent.SomethingWentWrong }
        )
    }

    fun selectRangesPack(packNumber: Int) {
        executeUseCase(
            { selectedRangesPackUseCase.execute(packNumber) },
            { _event.value = RangesPacksEvent.NewPackSelected(packNumber) },
            { _event.value = RangesPacksEvent.SomethingWentWrong }
        )
    }
}