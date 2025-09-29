package com.mmfsin.betweenminds.data.mappers

import com.mmfsin.betweenminds.data.models.QuestionDTO
import com.mmfsin.betweenminds.data.models.RangeDTO
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.SavedScore

fun QuestionDTO.toQuestion() = Question(
    text = text
)

fun List<QuestionDTO>.toQuestionsList() = this.map { it.toQuestion() }

fun RangeDTO.toRange() = Range(
    leftRange = leftRange,
    rightRange = rightRange
)

fun List<RangeDTO>.toRangesList() = this.map { it.toRange() }