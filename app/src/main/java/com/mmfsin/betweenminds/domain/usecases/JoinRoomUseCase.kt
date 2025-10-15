package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRoomRepository
import javax.inject.Inject

class JoinRoomUseCase @Inject constructor(private val repository: IOnlineRoomRepository) {
    suspend fun execute(roomId: String, gameType: String) = repository.joinRoom(roomId, gameType)
}