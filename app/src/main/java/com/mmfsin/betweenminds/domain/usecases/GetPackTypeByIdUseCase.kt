package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import com.mmfsin.betweenminds.domain.models.Pack
import javax.inject.Inject

class GetPackTypeByIdUseCase @Inject constructor(private val repository: IPacksRepository) {
    suspend fun execute(packId: String): Pack? = repository.getPackById(packId)
}