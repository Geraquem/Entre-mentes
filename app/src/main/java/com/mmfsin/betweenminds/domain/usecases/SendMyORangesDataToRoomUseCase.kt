package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRoomRepository
import com.mmfsin.betweenminds.domain.models.OnlineData
import javax.inject.Inject

class SendMyORangesDataToRoomUseCase @Inject constructor(private val repository: IOnlineRoomRepository) {

    suspend fun execute(onlineData: OnlineData) = repository.sendMyORangesDataToRoom(onlineData)
}