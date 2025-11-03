package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.data.repository.OfflineRepository
import com.mmfsin.betweenminds.domain.models.Range
import javax.inject.Inject

class GetPackRangesUseCase @Inject constructor(
    private val repository: OfflineRepository
) {
    suspend fun execute(packNumber: Int): List<Range> {
        val questions = repository.getRanges()
        return questions.filter { it.pack == packNumber }
    }
}