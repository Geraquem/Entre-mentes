package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import javax.inject.Inject

class SetFreePacksUseCase @Inject constructor(private val repository: IPacksRepository) {
    fun execute() = repository.setFreePacks()
}