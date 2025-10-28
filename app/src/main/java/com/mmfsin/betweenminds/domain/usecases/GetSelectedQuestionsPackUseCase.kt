package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import javax.inject.Inject

class GetSelectedQuestionsPackUseCase @Inject constructor(
    private val repository: IPacksRepository
) {
    fun execute(): Int = repository.getQuestionsPackId()
}