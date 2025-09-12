package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IPhrasesRepository
import javax.inject.Inject

class GetPhrasesUseCase @Inject constructor(
    private val repository: IPhrasesRepository
) {
    fun execute() = repository.getPhrases()//.shuffled()
}