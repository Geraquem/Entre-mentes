package com.mmfsin.betweenminds.presentation.choose

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.CreateRoomUseCase
import com.mmfsin.betweenminds.domain.usecases.GetSelectedDataPackUseCase
import com.mmfsin.betweenminds.domain.usecases.JoinRoomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseViewModel @Inject constructor(
    private val getSelectedDataPackUseCase: GetSelectedDataPackUseCase,
    private val createRoomUseCase: CreateRoomUseCase,
    private val joinRoomUseCase: JoinRoomUseCase
) : BaseViewModel<ChooseEvent>() {

    fun getSelectedPack(gameType: String) {
        executeUseCase(
            { getSelectedDataPackUseCase.execute(gameType) },
            { result -> _event.value = ChooseEvent.SelectedPack(result) },
            { _event.value = ChooseEvent.SomethingWentWrong }
        )
    }

    fun createRoom(gameType: String) {
        executeUseCase(
            { createRoomUseCase.execute(gameType) },
            { result ->
                if (result == null) _event.value = ChooseEvent.SomethingWentWrong
                else _event.value = ChooseEvent.RoomCreated(result)
            },
            { _event.value = ChooseEvent.SomethingWentWrong }
        )
    }

    fun joinRoom(roomId: String, gameType: String) {
        executeUseCase(
            { joinRoomUseCase.execute(roomId, gameType) },
            { result -> _event.value = ChooseEvent.JoinedToRoom(result, roomId) },
            { _event.value = ChooseEvent.SomethingWentWrong }
        )
    }
}