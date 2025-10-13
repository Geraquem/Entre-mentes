package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRangesRepository
import javax.inject.Inject

class SendMyORangesPointsUseCase @Inject constructor(private val repository: IOnlineRangesRepository) {
    suspend fun execute(roomId: String, isCreator: Boolean, points: Int) =
        repository.sendPoints(roomId, isCreator, points)
}