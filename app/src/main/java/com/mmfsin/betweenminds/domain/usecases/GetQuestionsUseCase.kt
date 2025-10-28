package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.data.repository.OfflineRepository
import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import com.mmfsin.betweenminds.domain.models.Question
import java.util.Random
import javax.inject.Inject

class GetQuestionsUseCase @Inject constructor(
    private val packRepository: IPacksRepository,
    private val offlineRepository: OfflineRepository
) {
    suspend fun execute(): List<Question> {
        val selectedPack = packRepository.getSelectedQPackId()
        val questions = offlineRepository.getQuestions()
        val r = questions.filter { it.pack == selectedPack }.shuffled(Random(System.nanoTime()))
        return r
    }
}