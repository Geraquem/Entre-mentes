package com.mmfsin.betweenminds.data.mappers

import com.mmfsin.betweenminds.data.models.SavedScoreDTO
import com.mmfsin.betweenminds.domain.models.SavedScore

fun createSavedScoreDTO(score: SavedScore) = SavedScoreDTO().apply {
    id = id
    playerOneName = score.playerOneName
    playerTwoName = score.playerTwoName
    points = score.points
    notes = score.notes
    date = score.date
    mode = score.mode
}