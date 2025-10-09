package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineRoomRepository
import javax.inject.Inject

class CreateRoomUseCase @Inject constructor(private val repository: IOnlineRoomRepository) {
    suspend fun execute() = repository.createRoom()
}