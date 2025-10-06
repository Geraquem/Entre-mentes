package com.mmfsin.betweenminds.presentation.choose.roomcode

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.WaitToJoinOtherPlayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RoomCodeViewModel @Inject constructor(
    private val waitToJoinOtherPlayerUseCase: WaitToJoinOtherPlayerUseCase
) : BaseViewModel<RoomCodeEvent>() {

    fun waitForOtherPlayer(roomId: String) {
        executeUseCase(
            { waitToJoinOtherPlayerUseCase.execute(roomId) },
            { _event.value = RoomCodeEvent.ListeningOtherPlayer },
            { _event.value = RoomCodeEvent.SomethingWentWrong }
        )
    }
}