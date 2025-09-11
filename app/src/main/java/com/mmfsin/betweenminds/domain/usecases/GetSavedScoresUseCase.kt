package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IScoreRepository
import javax.inject.Inject

class GetSavedScoresUseCase @Inject constructor(private val repository: IScoreRepository) {
    fun execute() = repository.getScores()
}