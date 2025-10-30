package com.mmfsin.betweenminds.domain.usecases

import android.content.Context
import com.mmfsin.betweenminds.domain.interfaces.IOfflineRepository
import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import com.mmfsin.betweenminds.domain.models.RangesPack
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetRangesPacksUseCase @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: IOfflineRepository,
    private val packsRepository: IPacksRepository,
) {
    suspend fun execute(): List<RangesPack> {
        val ranges = repository.getRanges()
        val packs = packsRepository.getRangesPack()

        val result = packs.map { pack ->
            pack.copy(ranges = ranges.filter { it.pack == pack.packNumber })
        }

        return result.sortedBy { it.packNumber }
    }
}