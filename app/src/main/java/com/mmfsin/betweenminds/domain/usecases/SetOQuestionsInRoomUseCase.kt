package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineQuestionsRepository
import com.mmfsin.betweenminds.domain.models.Question
import javax.inject.Inject

class SetOQuestionsInRoomUseCase @Inject constructor(private val repository: IOnlineQuestionsRepository) {
    suspend fun execute(
        roomId: String,
        names: Pair<String, String>,
        questions: List<Question>,
        gameNumber: Int
    ) = repository.setQuestionsInRoom(roomId, names, questions, gameNumber)
}