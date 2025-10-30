package com.mmfsin.betweenminds.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mmfsin.betweenminds.data.mappers.createQuestionsPacks
import com.mmfsin.betweenminds.data.mappers.createRangesPacks
import com.mmfsin.betweenminds.data.models.PackDTO
import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import com.mmfsin.betweenminds.domain.models.QuestionPack
import com.mmfsin.betweenminds.domain.models.RangesPack
import com.mmfsin.betweenminds.utils.PACKS
import com.mmfsin.betweenminds.utils.QUESTIONS
import com.mmfsin.betweenminds.utils.QUESTIONS_PACK
import com.mmfsin.betweenminds.utils.RANGES
import com.mmfsin.betweenminds.utils.RANGES_PACK
import com.mmfsin.betweenminds.utils.SHARED_PREFS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class PacksRepository @Inject constructor(
    @ApplicationContext val context: Context
) : IPacksRepository {

    private suspend fun getPacks(): List<PackDTO> {
        val latch = CountDownLatch(1)
        val packs = mutableListOf<PackDTO>()
        Firebase.firestore.collection(PACKS).get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    try {
                        doc.toObject(PackDTO::class.java).let { packs.add(it) }
                    } catch (e: Exception) {
                        Log.e("error", "error parsing pack")
                    }
                }
                latch.countDown()

            }.addOnFailureListener {
                latch.countDown()
            }

        withContext(Dispatchers.IO) { latch.await() }
        return packs
    }

    override suspend fun getQuestionsPack(): List<QuestionPack> {
        val packs = getPacks()
        return packs.filter { it.packType == QUESTIONS }.createQuestionsPacks()
    }

    override suspend fun getRangesPack(): List<RangesPack> {
        val packs = getPacks()
        return packs.filter { it.packType == RANGES }.createRangesPacks()
    }

    override fun getSelectedQPackId(): Int {
        return getSharedPreferences().getInt(QUESTIONS_PACK, 0)
    }

    override fun editSelectedQPackId(packNumber: Int) {
        val editor = getSharedPreferences().edit()
        editor.putInt(QUESTIONS_PACK, packNumber)
        editor.apply()
    }

    override fun getSelectedRPackId(): Int {
        return getSharedPreferences().getInt(RANGES_PACK, 0)
    }

    override fun editSelectedRPackId(packNumber: Int) {
        val editor = getSharedPreferences().edit()
        editor.putInt(RANGES_PACK, packNumber)
        editor.apply()
    }

    private fun getSharedPreferences() = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)


    /****************************************************************************************************/
    /****************************************************************************************************/
    /****************************************************************************************************/
    /****************************************************************************************************/
    /****************************************************************************************************/
    suspend fun insertDataInFirestore() {
        val db = Firebase.firestore
        val batch = db.batch()

        val listToInsert = listToInsert()

        val latch = CountDownLatch(1)

        val usersCollection = db.collection(PACKS)

        for (data in listToInsert) {
            val newDocRef = data["packId"]?.let { usersCollection.document(it.toString()) }
            if (newDocRef != null) {
                batch.set(newDocRef, data)
            }
        }

        batch.commit()
            .addOnSuccessListener {
                latch.countDown()
            }
            .addOnFailureListener {
                latch.countDown()
            }
        withContext(Dispatchers.IO) { latch.await() }
    }


    private fun listToInsert(): List<HashMap<String, Any>> {
        return listOf(
            hashMapOf(
                "packId" to "questions_pack_free",
                "packNumber" to 0,
                "packType" to "questions",
                "price" to "0.25",
                "icon" to "image_url",
                "title" to "Básico",
                "description" to "Contiene 50 preguntas de todo tipo bien variadas para que dkjfslkjgklfsdjdlk",
            ),
            hashMapOf(
                "packId" to "questions_pack_couples",
                "packNumber" to 1,
                "packType" to "questions",
                "icon" to "image_url",
                "title" to "Para parejas",
                "price" to "0.25",
                "description" to "Preguntas para que pongas en duda o refuerces tu relación ñlkfsñdlkjfñlskfñlsdlñ",
            ),
            hashMapOf(
                "packId" to "questions_pack_idk",
                "packNumber" to 2,
                "price" to "0.25",
                "packType" to "questions",
                "icon" to "image_url",
                "title" to "No sé que poner",
                "description" to "Descnoidjflksdj sdlkfjlksdj flksjd lkj sld l",
            ),
            hashMapOf(
                "packId" to "ranges_pack_free",
                "price" to "0.25",
                "packNumber" to 0,
                "packType" to "ranges",
                "icon" to "image_url",
                "title" to "Básico",
                "description" to "LAdklskdñldkfñlkslñfk ñlkfñls kdñ l",
            ),
            hashMapOf(
                "packId" to "ranges_pack_idk1",
                "packNumber" to 1,
                "price" to "0.25",
                "packType" to "ranges",
                "icon" to "image_url",
                "title" to "No sé uno mirar wavelenght",
                "description" to "alkjdksjd lkajs dlklkdaslkjdlkas jljslj laj sljd lkasjdljweoigpor ig",
            ),
            hashMapOf(
                "packId" to "ranges_pack_idk2",
                "packNumber" to 2,
                "packType" to "ranges",
                "price" to "0.25",
                "icon" to "image_url",
                "title" to "Mirar Wavelenght 2",
                "description" to "a´ldksañdkj ñlkdjf ñlkjsdlñ fjsdl lkñsdj lñksdj lkjfñdlsj ñlsj dñlkjgñlksj gñl",
            ),
        )
    }
}