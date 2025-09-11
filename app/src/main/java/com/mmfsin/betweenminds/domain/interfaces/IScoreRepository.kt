package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.SavedScore

interface IScoreRepository {
    fun saveNewScore(score: SavedScore)
    fun getScores(): List<SavedScore>
}