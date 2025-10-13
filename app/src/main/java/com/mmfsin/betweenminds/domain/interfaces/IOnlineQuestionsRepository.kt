package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.Question

interface IOnlineQuestionsRepository {
    suspend fun setQuestionsInRoom(
        roomId: String,
        names: Pair<String, String>,
        questions: List<Question>
    )
//    suspend fun waitOtherPlayerORanges(roomId: String, isCreator: Boolean): List<OnlineRoundData>
//    suspend fun sendPoints(roomId: String, isCreator: Boolean, points: Int)
//    suspend fun waitOtherPlayerPoints(roomId: String, isCreator: Boolean): Int
//    suspend fun restartGame(roomId: String)
//    suspend fun waitCreatorToRestartGame(roomId: String)
}