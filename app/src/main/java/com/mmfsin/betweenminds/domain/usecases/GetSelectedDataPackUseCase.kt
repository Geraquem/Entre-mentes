package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import javax.inject.Inject

class GetSelectedDataPackUseCase @Inject constructor(
    private val repository: IPacksRepository
) {
    suspend fun execute(gameType: String): Pair<String?, String?> =
        repository.getDataSelectedPack(gameType)
}