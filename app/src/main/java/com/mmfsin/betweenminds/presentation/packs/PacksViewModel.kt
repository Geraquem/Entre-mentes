package com.mmfsin.betweenminds.presentation.packs

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.CheckVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PacksViewModel @Inject constructor(
    private val checkVersionUseCase: CheckVersionUseCase
) : BaseViewModel<PacksEvent>() {

    fun dontknow() {
//        executeUseCase(
//            { checkVersionUseCase.execute() },
//            { _event.value = MenuEvent.VersionCompleted },
//            { _event.value = MenuEvent.SomethingWentWrong }
//        )
    }
}