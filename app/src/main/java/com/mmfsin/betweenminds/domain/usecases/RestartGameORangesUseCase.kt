package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRangesRepository
import javax.inject.Inject

class RestartGameORangesUseCase @Inject constructor(private val repository: IOnlineRangesRepository) {
    suspend fun execute(roomId: String) = repository.restartGame(roomId)
}