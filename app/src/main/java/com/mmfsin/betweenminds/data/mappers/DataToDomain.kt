package com.mmfsin.betweenminds.data.mappers

import com.mmfsin.betweenminds.data.models.QuestionDTO
import com.mmfsin.betweenminds.data.models.RangeDTO
import com.mmfsin.betweenminds.data.models.SavedScoreDTO
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.SavedScore

fun SavedScoreDTO.toSavedScore() = SavedScore(
    id = id,
    playerOneName = playerOneName,
    playerTwoName = playerTwoName,
    points = points,
    notes = notes,
    date = date,
    mode = mode
)

fun List<SavedScoreDTO>.toListSavedScore() = this.map { it.toSavedScore() }

fun QuestionDTO.toQuestion() = Question(
    text = text
)

fun List<QuestionDTO>.toQuestionsList() = this.map { it.toQuestion() }

fun RangeDTO.toRange() = Range(
    leftRange = leftRange,
    rightRange = rightRange
)

fun List<RangeDTO>.toRangesList() = this.map { it.toRange() }