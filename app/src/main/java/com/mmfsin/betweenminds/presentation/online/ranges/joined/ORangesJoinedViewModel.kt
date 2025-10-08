package com.mmfsin.betweenminds.presentation.online.ranges.joined

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.usecases.GetRangesUseCase
import com.mmfsin.betweenminds.domain.usecases.SendMyORangesDataToRoomUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitOtherPlayerORangesUseCase
import com.mmfsin.betweenminds.presentation.online.ranges.creator.ORangesCreatorEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ORangesJoinedViewModel @Inject constructor(
    private val getRangesUseCase: GetRangesUseCase,
    private val sendMyORangesDataToRoomUseCase: SendMyORangesDataToRoomUseCase,
    private val waitOtherPlayerORangesUseCase: WaitOtherPlayerORangesUseCase,
) : BaseViewModel<ORangesJoinedEvent>() {

    fun getRanges() {
        executeUseCase(
            { getRangesUseCase.execute() },
            { result -> _event.value = ORangesJoinedEvent.GetRanges(result) },
            { _event.value = ORangesJoinedEvent.SomethingWentWrong }
        )
    }

    fun sendMyDataToRoom(onlineData: OnlineData) {
        executeUseCase(
            { sendMyORangesDataToRoomUseCase.execute(onlineData) },
            { waitOtherPlayerRanges(onlineData.roomId, onlineData.isCreator) },
            { _event.value = ORangesJoinedEvent.SomethingWentWrong }
        )
    }

    private fun waitOtherPlayerRanges(roomId: String, isCreator: Boolean) {
        executeUseCase(
            { waitOtherPlayerORangesUseCase.execute(roomId, isCreator) },
            { result -> _event.value = ORangesJoinedEvent.OtherPlayerRanges(result) },
            { _event.value = ORangesJoinedEvent.SomethingWentWrong }
        )
    }
}