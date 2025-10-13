package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRangesRepository
import com.mmfsin.betweenminds.domain.models.OnlineRoundData
import javax.inject.Inject

class WaitOtherPlayerORangesUseCase @Inject constructor(private val repository: IOnlineRangesRepository) {
    suspend fun execute(roomId: String, isCreator: Boolean): List<OnlineRoundData> =
        repository.waitOtherPlayerORanges(roomId, isCreator)
}