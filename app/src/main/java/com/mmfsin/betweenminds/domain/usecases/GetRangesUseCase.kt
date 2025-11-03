package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.data.repository.OfflineRepository
import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import com.mmfsin.betweenminds.domain.models.Range
import java.util.Random
import javax.inject.Inject

class GetRangesUseCase @Inject constructor(
    private val packRepository: IPacksRepository,
    private val offlineRepository: OfflineRepository
) {
    suspend fun execute(): List<Range> {
        val selectedPack = packRepository.getSelectedRPackId()
        val ranges = offlineRepository.getRanges()
        return ranges.filter { it.pack == selectedPack }.shuffled(Random(System.nanoTime()))
    }
}