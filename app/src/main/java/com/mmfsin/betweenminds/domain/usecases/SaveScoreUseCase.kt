package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IScoreRepository
import com.mmfsin.betweenminds.domain.models.SavedScore
import javax.inject.Inject

class SaveScoreUseCase @Inject constructor(private val repository: IScoreRepository) {
    fun execute(score: SavedScore) = repository.saveNewScore(score)
}