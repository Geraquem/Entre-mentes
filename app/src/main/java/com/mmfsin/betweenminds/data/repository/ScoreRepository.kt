package com.mmfsin.betweenminds.data.repository

import com.mmfsin.betweenminds.data.mappers.createSavedScoreDTO
import com.mmfsin.betweenminds.data.mappers.toListSavedScore
import com.mmfsin.betweenminds.data.models.SavedScoreDTO
import com.mmfsin.betweenminds.domain.interfaces.IRealmDatabase
import com.mmfsin.betweenminds.domain.interfaces.IScoreRepository
import com.mmfsin.betweenminds.domain.models.SavedScore
import io.realm.kotlin.ext.query
import javax.inject.Inject

class ScoreRepository @Inject constructor(
    private val realmDatabase: IRealmDatabase
) : IScoreRepository {

    override fun saveNewScore(score: SavedScore) {
        realmDatabase.addObject { createSavedScoreDTO(score) }
    }

    override fun getScores(): List<SavedScore> {
        val scores = realmDatabase.getObjectsFromRealm { query<SavedScoreDTO>().find() }
        return scores.toListSavedScore()
    }
}