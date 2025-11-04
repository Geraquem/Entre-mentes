package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.data.repository.OfflineRepository
import com.mmfsin.betweenminds.domain.models.Range
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPackRangesUseCase @Inject constructor(
    private val repository: OfflineRepository
) {
    suspend fun execute(packNumber: Int): List<Range> = withContext(Dispatchers.Default) {
        val ranges = repository.getRanges()
        ranges.filter { it.pack == packNumber }
    }
}