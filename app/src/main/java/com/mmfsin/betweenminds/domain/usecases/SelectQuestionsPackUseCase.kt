package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import javax.inject.Inject

class SelectQuestionsPackUseCase @Inject constructor(
    private val repository: IPacksRepository
) {
    fun execute(packNumber: Int) = repository.editSelectedQPackId(packNumber)
}