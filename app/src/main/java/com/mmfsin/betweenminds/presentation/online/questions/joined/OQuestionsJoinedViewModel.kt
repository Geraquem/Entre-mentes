package com.mmfsin.betweenminds.presentation.online.questions.joined

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetOQuestionsAndNamesUseCase
import com.mmfsin.betweenminds.domain.usecases.RestartGameORangesUseCase
import com.mmfsin.betweenminds.domain.usecases.SendMyORangesPointsUseCase
import com.mmfsin.betweenminds.domain.usecases.SendOpinionOQuestionsToRoomUseCase
import com.mmfsin.betweenminds.domain.usecases.SetOQuestionsInRoomUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitOtherPlayerORangesPointsUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitOtherPlayerORangesUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitToRestartORangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OQuestionsJoinedViewModel @Inject constructor(
    private val getOQuestionsAndNamesUseCase: GetOQuestionsAndNamesUseCase,
    private val sendOpinionOQuestionsToRoomUseCase: SendOpinionOQuestionsToRoomUseCase,
    private val setOQuestionsInRoomUseCase: SetOQuestionsInRoomUseCase,
    private val waitOtherPlayerORangesUseCase: WaitOtherPlayerORangesUseCase,
    private val sendMyORangesPointsUseCase: SendMyORangesPointsUseCase,
    private val waitOtherPlayerORangesPointsUseCase: WaitOtherPlayerORangesPointsUseCase,
    private val restartGameORangesUseCase: RestartGameORangesUseCase,
    private val waitToRestartORangesUseCase: WaitToRestartORangesUseCase,
) : BaseViewModel<OQuestionsJoinedEvent>() {

    fun getQuestionsAndNames(roomId: String) {
        executeUseCase(
            { getOQuestionsAndNamesUseCase.execute(roomId) },
            { result -> _event.value = OQuestionsJoinedEvent.GetQuestionsAndNames(result) },
            { _event.value = OQuestionsJoinedEvent.SomethingWentWrong }
        )
    }

    fun sendOpinionToRoom(roomId: String, round: Int, orangeOpinion: Int) {
        executeUseCase(
            {
                sendOpinionOQuestionsToRoomUseCase.execute(
                    roomId,
                    isCreator = false,
                    round,
                    orangeOpinion
                )
            },
            {},
            { _event.value = OQuestionsJoinedEvent.SomethingWentWrong }
        )
    }
}