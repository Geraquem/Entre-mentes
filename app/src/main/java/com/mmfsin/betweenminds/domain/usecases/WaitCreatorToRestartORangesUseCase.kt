package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRangesRepository
import javax.inject.Inject

class WaitCreatorToRestartORangesUseCase @Inject constructor(private val repository: IOnlineRangesRepository) {
    suspend fun execute(roomId: String) = repository.waitCreatorToRestartGame(roomId)
}