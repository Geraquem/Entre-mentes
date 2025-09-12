package com.mmfsin.betweenminds.data.repository

import com.mmfsin.betweenminds.domain.interfaces.IPhrasesRepository
import com.mmfsin.betweenminds.domain.interfaces.IRealmDatabase
import com.mmfsin.betweenminds.domain.models.Phrase
import javax.inject.Inject

class PhrasesRepository @Inject constructor(
    private val realmDatabase: IRealmDatabase
) : IPhrasesRepository {

    override fun getPhrases(): List<Phrase> {
        return listOf(
            Phrase(text = "Frase numero 1"),
            Phrase(text = "Frase numero 2"),
            Phrase(text = "Frase numero 3"),
            Phrase(text = "Frase numero 4")
        )//.shuffled()
    }
}