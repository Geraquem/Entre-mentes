package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRangesRepository
import com.mmfsin.betweenminds.domain.models.OnlineData
import javax.inject.Inject

class SendMyORangesDataToRoomUseCase @Inject constructor(private val repository: IOnlineRangesRepository) {
    suspend fun execute(onlineData: OnlineData) = repository.sendMyORangesDataToRoom(onlineData)
}