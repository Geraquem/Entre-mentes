package com.mmfsin.betweenminds.presentation.online.ranges.creator

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.usecases.GetRangesUseCase
import com.mmfsin.betweenminds.domain.usecases.SendMyORangesDataToRoomUseCase
import com.mmfsin.betweenminds.domain.usecases.SendMyORangesPointsUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitOtherPlayerORangesPointsUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitOtherPlayerORangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ORangesCreatorViewModel @Inject constructor(
    private val getRangesUseCase: GetRangesUseCase,
    private val sendMyORangesDataToRoomUseCase: SendMyORangesDataToRoomUseCase,
    private val waitOtherPlayerORangesUseCase: WaitOtherPlayerORangesUseCase,
    private val sendMyORangesPointsUseCase: SendMyORangesPointsUseCase,
    private val waitOtherPlayerORangesPointsUseCase: WaitOtherPlayerORangesPointsUseCase
) : BaseViewModel<ORangesCreatorEvent>() {

    fun getRanges() {
        executeUseCase(
            { getRangesUseCase.execute() },
            { result -> _event.value = ORangesCreatorEvent.GetRanges(result) },
            { _event.value = ORangesCreatorEvent.SomethingWentWrong }
        )
    }

    fun sendMyDataToRoom(onlineData: OnlineData) {
        executeUseCase(
            { sendMyORangesDataToRoomUseCase.execute(onlineData) },
            { waitOtherPlayerRanges(onlineData.roomId, onlineData.isCreator) },
            { _event.value = ORangesCreatorEvent.SomethingWentWrong }
        )
    }

    private fun waitOtherPlayerRanges(roomId: String, isCreator: Boolean) {
        executeUseCase(
            { waitOtherPlayerORangesUseCase.execute(roomId, isCreator) },
            { result -> _event.value = ORangesCreatorEvent.OtherPlayerData(result) },
            { _event.value = ORangesCreatorEvent.SomethingWentWrong }
        )
    }

    fun sendMyPoints(roomId: String, isCreator: Boolean, points: Int) {
        executeUseCase(
            { sendMyORangesPointsUseCase.execute(roomId, isCreator, points) },
            { waitOtherPlayerToFinish(roomId, isCreator) },
            { _event.value = ORangesCreatorEvent.SomethingWentWrong }
        )
    }

    private fun waitOtherPlayerToFinish(roomId: String, isCreator: Boolean) {
        executeUseCase(
            { waitOtherPlayerORangesPointsUseCase.execute(roomId, isCreator) },
            { result -> _event.value = ORangesCreatorEvent.OtherPlayerPoints(result) },
            { _event.value = ORangesCreatorEvent.SomethingWentWrong }
        )
    }
}