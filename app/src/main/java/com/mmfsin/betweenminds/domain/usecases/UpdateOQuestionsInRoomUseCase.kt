package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOnlineQuestionsRepository
import com.mmfsin.betweenminds.domain.models.Question
import javax.inject.Inject

class UpdateOQuestionsInRoomUseCase @Inject constructor(private val repository: IOnlineQuestionsRepository) {
    suspend fun execute(roomId: String, questions: List<Question>, gameNumber: Int) =
        repository.updateQuestions(roomId, questions, gameNumber)
}