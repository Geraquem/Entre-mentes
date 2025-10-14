package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineQuestionsRepository
import javax.inject.Inject

class WaitOtherPlayerOpinionOQuestionsUseCase @Inject constructor(private val repository: IOnlineQuestionsRepository) {
    suspend fun execute(roomId: String, isCreator: Boolean, round: Int) =
        repository.waitOtherPlayerOpinion(roomId, isCreator, round)
}