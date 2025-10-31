package com.mmfsin.betweenminds.domain.models

data class QuestionsPack(
    var packId: String = "",
    var packNumber: Int = 0,
    var packTitle: String,
    var packDescription: String,
    var packPrice: String,
    var packIcon: String,
    var selected: Boolean = false,
    var purchased: Boolean = false,
    var questions: List<Question> = emptyList()
)
