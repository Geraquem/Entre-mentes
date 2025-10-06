package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRoomRepository
import javax.inject.Inject

class WaitToJoinOtherPlayerUseCase @Inject constructor(private val repository: IOnlineRoomRepository) {
    suspend fun execute(roomId: String) = repository.waitToJoinRoom(roomId)
}