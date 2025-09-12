package com.mmfsin.betweenminds.presentation.scores

import com.mmfsin.betweenminds.domain.models.SavedScore

sealed class SavedScoresEvent {
    data class Scores(val scores: List<SavedScore>) : SavedScoresEvent()
    data class ScoreDeleted(val savedId: String) : SavedScoresEvent()
    data object SomethingWentWrong : SavedScoresEvent()
}