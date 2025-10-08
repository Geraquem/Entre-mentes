package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.models.OnlineRoundData

interface IOnlineRoomRepository {
    suspend fun createRoom(userName: String): String?
    suspend fun joinRoom(userName: String, roomId: String): Boolean
    suspend fun waitToJoinRoom(roomId: String)

    suspend fun sendMyORangesDataToRoom(onlineData: OnlineData)
    suspend fun waitOtherPlayerORanges(roomId: String, isCreator: Boolean): List<OnlineRoundData>
}