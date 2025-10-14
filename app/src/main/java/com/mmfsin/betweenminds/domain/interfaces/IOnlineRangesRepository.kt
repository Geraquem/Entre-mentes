package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.models.OnlineRoundData

interface IOnlineRangesRepository {
    suspend fun sendMyORangesDataToRoom(onlineData: OnlineData)
    suspend fun waitOtherPlayerORanges(roomId: String, isCreator: Boolean): List<OnlineRoundData>
    suspend fun sendPoints(roomId: String, isCreator: Boolean, points: Int)
    suspend fun waitOtherPlayerPoints(roomId: String, isCreator: Boolean): Int
}