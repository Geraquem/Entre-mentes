package com.mmfsin.betweenminds.presentation.online.questions.creator

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.usecases.GetQuestionsUseCase
import com.mmfsin.betweenminds.domain.usecases.SendOpinionOQuestionsToRoomUseCase
import com.mmfsin.betweenminds.domain.usecases.SetOQuestionsInRoomUseCase
import com.mmfsin.betweenminds.domain.usecases.WaitOtherPlayerOpinionOQuestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OQuestionsCreatorViewModel @Inject constructor(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val setOQuestionsInRoomUseCase: SetOQuestionsInRoomUseCase,
    private val sendOpinionOQuestionsToRoomUseCase: SendOpinionOQuestionsToRoomUseCase,
    private val waitOtherPlayerOpinionOQuestionsUseCase: WaitOtherPlayerOpinionOQuestionsUseCase,
) : BaseViewModel<OQuestionsCreatorEvent>() {

    fun getQuestions() {
        executeUseCase(
            { getQuestionsUseCase.execute() },
            { result -> _event.value = OQuestionsCreatorEvent.GetQuestionsCreator(result) },
            { _event.value = OQuestionsCreatorEvent.SomethingWentWrong }
        )
    }

    fun setQuestionsInRoom(roomId: String, names: Pair<String, String>, questions: List<Question>) {
        executeUseCase(
            { setOQuestionsInRoomUseCase.execute(roomId, names, questions) },
            { _event.value = OQuestionsCreatorEvent.QuestionsCreatorSetInRoom },
            { _event.value = OQuestionsCreatorEvent.SomethingWentWrong }
        )
    }

    fun sendOpinionToRoom(roomId: String, round: Int, blueOpinion: Int) {
        executeUseCase(
            {
                sendOpinionOQuestionsToRoomUseCase.execute(
                    roomId,
                    isCreator = true,
                    round,
                    blueOpinion
                )
            },
            { waitToOtherPlayerOpinion(roomId, round) },
            { _event.value = OQuestionsCreatorEvent.SomethingWentWrong }
        )
    }

    private fun waitToOtherPlayerOpinion(roomId: String, round: Int) {
        executeUseCase(
            { waitOtherPlayerOpinionOQuestionsUseCase.execute(roomId, isCreator = true, round) },
            { result -> _event.value = OQuestionsCreatorEvent.OtherPlayerOpinion(result) },
            { _event.value = OQuestionsCreatorEvent.SomethingWentWrong }
        )
    }
}