package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import javax.inject.Inject

class CheckIfFreePacksUseCase @Inject constructor(private val repository: IPacksRepository) {
    fun execute() = repository.checkIfPacksAreFree()
}