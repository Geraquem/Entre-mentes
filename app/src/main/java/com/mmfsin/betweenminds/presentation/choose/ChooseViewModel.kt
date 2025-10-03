package com.mmfsin.betweenminds.presentation.choose

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.CheckVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseViewModel @Inject constructor(
) : BaseViewModel<ChooseEvent>() {

    fun checkVersion() {
//        executeUseCase(
//            { checkVersionUseCase.execute() },
//            { _event.value = ChooseEvent.VersionCompleted },
//            { _event.value = ChooseEvent.SomethingWentWrong }
//        )
    }
}