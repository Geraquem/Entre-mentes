package com.mmfsin.betweenminds.presentation.packs

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.CheckIfFreePacksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PacksVPViewModel @Inject constructor(
    private val checkIfFreePacksUseCase: CheckIfFreePacksUseCase,
) : BaseViewModel<PacksVPEvent>() {

    fun checkIfPacksAreFree() {
        executeUseCase(
            { checkIfFreePacksUseCase.execute() },
            { result -> _event.value = PacksVPEvent.FreePack(result) },
            { _event.value = PacksVPEvent.FreePack(false) }
        )
    }
}