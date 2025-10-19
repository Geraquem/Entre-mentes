package com.mmfsin.betweenminds.presentation.questions.online.joined

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetOQuestionsAndNamesUseCase
import com.mmfsin.betweenminds.domain.usecases.SendOpinionOQuestionsToRoomUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitCreatorToRestartOQuestionsUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitOtherPlayerOpinionOQuestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OQuestionsJoinedViewModel @Inject constructor(
    private val getOQuestionsAndNamesUseCase: GetOQuestionsAndNamesUseCase,
    private val sendOpinionOQuestionsToRoomUseCase: SendOpinionOQuestionsToRoomUseCase,
    private val waitOtherPlayerOpinionOQuestionsUseCase: WaitOtherPlayerOpinionOQuestionsUseCase,
    private val waitCreatorToRestartOQuestionsUseCase: WaitCreatorToRestartOQuestionsUseCase
) : BaseViewModel<OQuestionsJoinedEvent>() {

    fun getQuestionsAndNames(roomId: String) {
        executeUseCase(
            { getOQuestionsAndNamesUseCase.execute(roomId) },
            { result -> _event.value = OQuestionsJoinedEvent.GetQuestionsAndNames(result) },
            { _event.value = OQuestionsJoinedEvent.SomethingWentWrong }
        )
    }

    fun sendOpinionToRoom(roomId: String, round: Int, orangeOpinion: Float) {
        executeUseCase(
            {
                sendOpinionOQuestionsToRoomUseCase.execute(
                    roomId,
                    isCreator = false,
                    round,
                    orangeOpinion
                )
            },
            { waitToOtherPlayerOpinion(roomId, round) },
            { _event.value = OQuestionsJoinedEvent.SomethingWentWrong }
        )
    }

    private fun waitToOtherPlayerOpinion(roomId: String, round: Int) {
        executeUseCase(
            { waitOtherPlayerOpinionOQuestionsUseCase.execute(roomId, isCreator = false, round) },
            { result -> _event.value = OQuestionsJoinedEvent.OtherPlayerOpinion(result) },
            { _event.value = OQuestionsJoinedEvent.SomethingWentWrong }
        )
    }

    fun waitCreatorToRestartGame(roomId: String, gameNumber: Int) {
        executeUseCase(
            { waitCreatorToRestartOQuestionsUseCase.execute(roomId, gameNumber) },
            { result->_event.value = OQuestionsJoinedEvent.GameRestarted(result) },
            { _event.value = OQuestionsJoinedEvent.SomethingWentWrong }
        )
    }
}