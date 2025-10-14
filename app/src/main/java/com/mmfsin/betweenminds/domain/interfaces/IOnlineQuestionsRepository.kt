package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.OnlineQuestionsAndNames
import com.mmfsin.betweenminds.domain.models.Question

interface IOnlineQuestionsRepository {
    suspend fun setQuestionsInRoom(
        roomId: String,
        names: Pair<String, String>,
        questions: List<Question>,
        gameNumber: Int
    )

    suspend fun updateQuestions(
        roomId: String,
        questions: List<Question>,
        gameNumber: Int
    )

    suspend fun getQuestionsAndNames(roomId: String): OnlineQuestionsAndNames
    suspend fun sendOpinionOQuestionsToRoomUseCase(
        roomId: String,
        isCreator: Boolean,
        round: Int,
        orangeOpinion: Int
    )

    suspend fun waitOtherPlayerOpinion(roomId: String, isCreator: Boolean, round: Int): Int
    suspend fun waitCreatorToRestartGame(roomId: String, gameNumber: Int): Int
}