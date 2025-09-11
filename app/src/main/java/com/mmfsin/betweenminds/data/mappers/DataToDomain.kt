package com.mmfsin.betweenminds.data.mappers

import com.mmfsin.betweenminds.data.models.SavedScoreDTO
import com.mmfsin.betweenminds.domain.models.SavedScore

fun SavedScoreDTO.toSavedScore() = SavedScore(
    playerOneName = playerOneName,
    playerTwoName = playerTwoName,
    points = points,
    notes = notes,
    date = date,
    mode = mode
)

fun List<SavedScoreDTO>.toListSavedScore() = this.map { it.toSavedScore() }