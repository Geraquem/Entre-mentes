package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import com.mmfsin.betweenminds.domain.models.Range
import javax.inject.Inject

class GetPackRangesUseCase @Inject constructor(
    private val repository: IPacksRepository,
) {
    suspend fun execute(packId: String): List<Range> = repository.getPackRanges(packId)
}