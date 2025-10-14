package com.mmfsin.betweenminds.presentation.online.questions.joined

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetOQuestionsAndNamesUseCase
import com.mmfsin.betweenminds.domain.usecases.SendOpinionOQuestionsToRoomUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitOtherPlayerOpinionOQuestionsUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitToRestartGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OQuestionsJoinedViewModel @Inject constructor(
    private val getOQuestionsAndNamesUseCase: GetOQuestionsAndNamesUseCase,
    private val sendOpinionOQuestionsToRoomUseCase: SendOpinionOQuestionsToRoomUseCase,
    private val waitOtherPlayerOpinionOQuestionsUseCase: WaitOtherPlayerOpinionOQuestionsUseCase,
    private val waitToRestartGameUseCase: WaitToRestartGameUseCase,
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

    fun waitCreatorToRestartGame(roomId: String) {
        executeUseCase(
            { waitToRestartGameUseCase.execute(roomId) },
            { _event.value = OQuestionsJoinedEvent.GameRestarted },
            { _event.value = OQuestionsJoinedEvent.SomethingWentWrong }
        )
    }
}