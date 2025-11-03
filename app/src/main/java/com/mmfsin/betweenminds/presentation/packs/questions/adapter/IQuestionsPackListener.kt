package com.mmfsin.betweenminds.presentation.packs.questions.adapter

import com.mmfsin.betweenminds.domain.models.QuestionsPack

interface IQuestionsPackListener {
    fun selectPack(packNumber: Int)
    fun seeMore(pack: QuestionsPack)
}