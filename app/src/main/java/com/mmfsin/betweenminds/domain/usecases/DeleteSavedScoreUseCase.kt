package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IScoreRepository
import javax.inject.Inject

class DeleteSavedScoreUseCase @Inject constructor(private val repository: IScoreRepository) {
    fun execute(savedId: String) = repository.deleteSavedScore(savedId)
}