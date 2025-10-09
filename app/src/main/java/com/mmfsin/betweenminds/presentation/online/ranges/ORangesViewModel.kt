package com.mmfsin.betweenminds.presentation.online.ranges

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.usecases.GetRangesUseCase
import com.mmfsin.betweenminds.domain.usecases.RestartGameORangesUseCase
import com.mmfsin.betweenminds.domain.usecases.SendMyORangesDataToRoomUseCase
import com.mmfsin.betweenminds.domain.usecases.SendMyORangesPointsUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitOtherPlayerORangesPointsUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitOtherPlayerORangesUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitToRestartORangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ORangesViewModel @Inject constructor(
    private val getRangesUseCase: GetRangesUseCase,
    private val sendMyORangesDataToRoomUseCase: SendMyORangesDataToRoomUseCase,
    private val waitOtherPlayerORangesUseCase: WaitOtherPlayerORangesUseCase,
    private val sendMyORangesPointsUseCase: SendMyORangesPointsUseCase,
    private val waitOtherPlayerORangesPointsUseCase: WaitOtherPlayerORangesPointsUseCase,
    private val restartGameORangesUseCase: RestartGameORangesUseCase,
    private val waitToRestartORangesUseCase: WaitToRestartORangesUseCase,
) : BaseViewModel<ORangesEvent>() {

    fun getRanges() {
        executeUseCase(
            { getRangesUseCase.execute() },
            { result -> _event.value = ORangesEvent.GetRanges(result) },
            { _event.value = ORangesEvent.SomethingWentWrong }
        )
    }

    fun sendMyDataToRoom(onlineData: OnlineData) {
        executeUseCase(
            { sendMyORangesDataToRoomUseCase.execute(onlineData) },
            { waitOtherPlayerRanges(onlineData.roomId, onlineData.isCreator) },
            { _event.value = ORangesEvent.SomethingWentWrong }
        )
    }

    private fun waitOtherPlayerRanges(roomId: String, isCreator: Boolean) {
        executeUseCase(
            { waitOtherPlayerORangesUseCase.execute(roomId, isCreator) },
            { result -> _event.value = ORangesEvent.OtherPlayerData(result) },
            { _event.value = ORangesEvent.SomethingWentWrong }
        )
    }

    fun sendMyPoints(roomId: String, isCreator: Boolean, points: Int) {
        executeUseCase(
            { sendMyORangesPointsUseCase.execute(roomId, isCreator, points) },
            { waitOtherPlayerToFinish(roomId, isCreator) },
            { _event.value = ORangesEvent.SomethingWentWrong }
        )
    }

    private fun waitOtherPlayerToFinish(roomId: String, isCreator: Boolean) {
        executeUseCase(
            { waitOtherPlayerORangesPointsUseCase.execute(roomId, isCreator) },
            { result -> _event.value = ORangesEvent.OtherPlayerPoints(result) },
            { _event.value = ORangesEvent.SomethingWentWrong }
        )
    }

    fun restartGame(roomId: String) {
        executeUseCase(
            { restartGameORangesUseCase.execute(roomId) },
            { _event.value = ORangesEvent.GameRestarted },
            { _event.value = ORangesEvent.SomethingWentWrong }
        )
    }

    fun waitToCreatorToRestart(roomId: String) {
        executeUseCase(
            { waitToRestartORangesUseCase.execute(roomId) },
            { _event.value = ORangesEvent.GameRestarted },
            { _event.value = ORangesEvent.SomethingWentWrong }
        )
    }
}