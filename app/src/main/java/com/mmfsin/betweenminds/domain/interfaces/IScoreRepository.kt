package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.SavedScore

interface IScoreRepository {
    fun getScores(): List<SavedScore>
    fun saveNewScore(score: SavedScore)
    fun deleteSavedScore(scoreId: String)
}