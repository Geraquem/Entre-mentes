package com.mmfsin.betweenminds.presentation.online.ranges.creator

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.usecases.GetRangesUseCase
import com.mmfsin.betweenminds.domain.usecases.SendDataToOtherPlayerUseCase
import com.mmfsin.betweenminds.presentation.online.ranges.joined.ORangesJoinedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ORangesCreatorViewModel @Inject constructor(
    private val getRangesUseCase: GetRangesUseCase,
    private val sendDataToOtherPlayerUseCase: SendDataToOtherPlayerUseCase,
) : BaseViewModel<ORangesCreatorEvent>() {

    fun getRanges() {
        executeUseCase(
            { getRangesUseCase.execute() },
            { result -> _event.value = ORangesCreatorEvent.GetRanges(result) },
            { _event.value = ORangesCreatorEvent.SomethingWentWrong }
        )
    }

    fun sendDataToOtherPlayer(onlineData: OnlineData) {
        executeUseCase(
            { sendDataToOtherPlayerUseCase.execute(onlineData) },
            {},
            { _event.value = ORangesCreatorEvent.SomethingWentWrong }
        )
    }
}