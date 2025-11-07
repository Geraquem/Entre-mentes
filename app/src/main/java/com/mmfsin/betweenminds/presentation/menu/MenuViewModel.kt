package com.mmfsin.betweenminds.presentation.menu

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.CheckVersionUseCase
import com.mmfsin.betweenminds.domain.usecases.SetFreePacksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val checkVersionUseCase: CheckVersionUseCase,
    private val setFreePacksUseCase: SetFreePacksUseCase
) : BaseViewModel<MenuEvent>() {

    fun checkVersion() {
        executeUseCase(
            { checkVersionUseCase.execute() },
            { _event.value = MenuEvent.VersionCompleted },
            { _event.value = MenuEvent.SomethingWentWrong }
        )
    }

    fun setFreePacks() {
        executeUseCase(
            { setFreePacksUseCase.execute() },
            { _event.value = MenuEvent.FreePacks },
            { _event.value = MenuEvent.SomethingWentWrong }
        )
    }
}