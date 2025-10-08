package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRoomRepository
import com.mmfsin.betweenminds.domain.models.OnlineRoundData
import javax.inject.Inject

class WaitOtherPlayerORangesPointsUseCase @Inject constructor(private val repository: IOnlineRoomRepository) {
    suspend fun execute(roomId: String, isCreator: Boolean): Int =
        repository.waitOtherPlayerPoints(roomId, isCreator)
}