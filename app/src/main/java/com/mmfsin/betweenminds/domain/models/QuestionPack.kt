package com.mmfsin.betweenminds.domain.models

open class QuestionPack(
    var packId: Long = 0,
    var packName: String = "",
    var packTitle: Int = 0,
    var packDescription: Int = 0,
    var questions: List<Question> = emptyList()
)
