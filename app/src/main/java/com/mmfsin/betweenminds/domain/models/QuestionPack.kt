package com.mmfsin.betweenminds.domain.models

data class QuestionPack(
    var packId: String = "",
    var packNumber: Int = 0,
    var packTitle: String,
    var packDescription: String,
    var selected: Boolean = false,
    var purchased: Boolean = false,
    var questions: List<Question> = emptyList()
)
