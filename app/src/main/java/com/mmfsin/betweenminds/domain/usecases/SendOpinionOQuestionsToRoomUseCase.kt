package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineQuestionsRepository
import javax.inject.Inject

class SendOpinionOQuestionsToRoomUseCase @Inject constructor(private val repository: IOnlineQuestionsRepository) {
    suspend fun execute(roomId: String, isCreator: Boolean, round: Int, orangeOpinion: Int) =
        repository.sendOpinionOQuestionsToRoomUseCase(roomId, isCreator, round, orangeOpinion)
}