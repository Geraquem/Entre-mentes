package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.OnlineQuestionsAndNames
import com.mmfsin.betweenminds.domain.models.Question

interface IOnlineQuestionsRepository {
    suspend fun setQuestionsInRoom(
        roomId: String,
        names: Pair<String, String>,
        questions: List<Question>
    )

    suspend fun getQuestionsAndNames(roomId: String): OnlineQuestionsAndNames
    suspend fun sendOpinionOQuestionsToRoomUseCase(
        roomId: String,
        isCreator: Boolean,
        round: Int,
        orangeOpinion: Int
    )

    suspend fun waitOtherPlayerOpinion(roomId: String, isCreator: Boolean, round: Int): Int
}