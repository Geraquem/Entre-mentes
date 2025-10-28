package com.mmfsin.betweenminds.domain.models

open class QuestionPack(
    var packId: Int = 0,
    var packName: String,
    var packTitle: Int,
    var packDescription: Int,
    var selected: Boolean = false,
    var questions: List<Question> = emptyList()
)
