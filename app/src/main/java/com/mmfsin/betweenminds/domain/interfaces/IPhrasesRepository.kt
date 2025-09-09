package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.Phrase

interface IPhrasesRepository {
    fun getPhrases(): List<Phrase>
}