package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineQuestionsRepository
import javax.inject.Inject

class GetOQuestionsAndNamesUseCase @Inject constructor(
    private val repository: IOnlineQuestionsRepository
) {
    suspend fun execute(roomId: String) = repository.getQuestionsAndNames(roomId)
}