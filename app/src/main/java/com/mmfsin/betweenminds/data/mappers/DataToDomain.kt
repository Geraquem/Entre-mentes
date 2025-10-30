package com.mmfsin.betweenminds.data.mappers

import com.mmfsin.betweenminds.data.models.PackDTO
import com.mmfsin.betweenminds.data.models.QuestionDTO
import com.mmfsin.betweenminds.data.models.RangeDTO
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.QuestionPack
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.RangesPack

fun QuestionDTO.toQuestion() = Question(
    question = question,
    pack = pack
)

fun List<QuestionDTO>.toQuestionsList() = this.map { it.toQuestion() }

fun RangeDTO.toRange() = Range(
    leftRange = leftRange,
    rightRange = rightRange,
    pack = pack
)

fun List<RangeDTO>.toRangesList() = this.map { it.toRange() }


fun PackDTO.toQuestionPack() = QuestionPack(
    packId = packId,
    packNumber = packNumber.toInt(),
    packTitle = title,
    packDescription = description
)

fun List<PackDTO>.createQuestionsPacks() = this.map { it.toQuestionPack() }

fun PackDTO.toRangesPack() = RangesPack(
    packId = packId,
    packNumber = packNumber.toInt(),
    packTitle = title,
    packDescription = description
)

fun List<PackDTO>.createRangesPacks() = this.map { it.toRangesPack() }

