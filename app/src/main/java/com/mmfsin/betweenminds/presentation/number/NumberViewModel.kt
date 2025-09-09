package com.mmfsin.betweenminds.presentation.number

import com.mmfsin.betweenminds.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NumberViewModel @Inject constructor(
) : BaseViewModel<NumberEvent>() {

    fun checkVersion() {
//        executeUseCase(
//            { checkVersionUseCase.execute() },
//            { _event.value = MenuEvent.Completed },
//            { _event.value = MenuEvent.SomethingWentWrong }
//        )
    }
}