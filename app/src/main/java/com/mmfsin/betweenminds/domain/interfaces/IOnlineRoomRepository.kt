package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.models.OnlineRoundData

interface IOnlineRoomRepository {
    suspend fun createRoom(): String?
    suspend fun joinRoom(roomId: String): Boolean
    suspend fun waitToJoinRoom(roomId: String)

    suspend fun sendMyORangesDataToRoom(onlineData: OnlineData)
    suspend fun waitOtherPlayerORanges(roomId: String, isCreator: Boolean): List<OnlineRoundData>
    suspend fun sendPoints(roomId: String, isCreator: Boolean, points: Int)
    suspend fun waitOtherPlayerPoints(roomId: String, isCreator: Boolean): Int
    suspend fun restartGame(roomId: String)
    suspend fun waitCreatorToRestartGame(roomId: String)
}