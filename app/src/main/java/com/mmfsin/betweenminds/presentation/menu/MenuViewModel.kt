package com.mmfsin.betweenminds.presentation.menu

import com.mmfsin.betweenminds.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
) : BaseViewModel<MenuEvent>() {

    fun checkVersion() {
//        executeUseCase(
//            { checkVersionUseCase.execute() },
//            { _event.value = MenuEvent.Completed },
//            { _event.value = MenuEvent.SomethingWentWrong }
//        )
    }
}