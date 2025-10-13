package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRangesRepository
import javax.inject.Inject

class WaitOtherPlayerORangesPointsUseCase @Inject constructor(private val repository: IOnlineRangesRepository) {
    suspend fun execute(roomId: String, isCreator: Boolean): Int =
        repository.waitOtherPlayerPoints(roomId, isCreator)
}