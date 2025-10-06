package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRoomRepository
import javax.inject.Inject

class JoinRoomUseCase @Inject constructor(private val repository: IOnlineRoomRepository) {
    suspend fun execute(userName: String, roomId: String) = repository.joinRoom(userName, roomId)
}