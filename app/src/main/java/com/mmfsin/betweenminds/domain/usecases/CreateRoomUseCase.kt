package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRepository
import javax.inject.Inject

class CreateRoomUseCase @Inject constructor(private val repository: IOnlineRepository) {
    suspend fun execute(userName: String) = repository.createRoom(userName)
}