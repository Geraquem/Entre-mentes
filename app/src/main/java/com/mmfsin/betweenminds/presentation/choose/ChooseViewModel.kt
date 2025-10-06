package com.mmfsin.betweenminds.presentation.choose

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.CreateRoomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseViewModel @Inject constructor(
    private val createRoomUseCase: CreateRoomUseCase
) : BaseViewModel<ChooseEvent>() {

    fun createRoom(userName: String) {
        executeUseCase(
            { createRoomUseCase.execute(userName) },
            { result ->
                if (result == null) _event.value = ChooseEvent.SomethingWentWrong
                else _event.value = ChooseEvent.RoomCreated(result)
            },
            { _event.value = ChooseEvent.SomethingWentWrong }
        )
    }
}