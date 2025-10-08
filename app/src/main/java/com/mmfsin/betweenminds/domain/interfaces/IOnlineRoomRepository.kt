package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.OnlineData

interface IOnlineRoomRepository {
    suspend fun createRoom(userName: String): String?
    suspend fun joinRoom(userName: String, roomId: String): Boolean
    suspend fun waitToJoinRoom(roomId: String)

    suspend fun sendDataToOtherPlayer(onlineData: OnlineData)
}