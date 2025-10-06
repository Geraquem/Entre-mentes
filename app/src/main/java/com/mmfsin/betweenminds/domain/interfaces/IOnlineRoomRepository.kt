package com.mmfsin.betweenminds.domain.interfaces

import kotlinx.coroutines.flow.Flow

interface IOnlineRoomRepository {
    suspend fun createRoom(userName: String): String?
    suspend fun joinRoom(userName: String, roomId: String): Boolean

    suspend fun waitToJoinRoom(roomId: String)
}