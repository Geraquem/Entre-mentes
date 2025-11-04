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
import com.mmfsin.betweenminds.domain.interfaces.IRealmDatabase
import com.mmfsin.betweenminds.domain.models.QuestionsPack
import com.mmfsin.betweenminds.domain.models.RangesPack
import com.mmfsin.betweenminds.utils.PACKS
import com.mmfsin.betweenminds.utils.QUESTIONS
import com.mmfsin.betweenminds.utils.QUESTIONS_PACK
import com.mmfsin.betweenminds.utils.QUESTIONS_TYPE
import com.mmfsin.betweenminds.utils.RANGES
import com.mmfsin.betweenminds.utils.RANGES_PACK
import com.mmfsin.betweenminds.utils.RANGES_TYPE
import com.mmfsin.betweenminds.utils.SERVER_PACKS
import com.mmfsin.betweenminds.utils.SHARED_PREFS
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class PacksRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val realmDatabase: IRealmDatabase
) : IPacksRepository {

    private suspend fun getPacks(): List<PackDTO> {
        val packs = mutableListOf<PackDTO>()
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)

        if (sharedPrefs.getBoolean(SERVER_PACKS, true)) {
            val latch = CountDownLatch(1)
            Firebase.firestore.collection(PACKS).get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        try {
                            doc.toObject(PackDTO::class.java).let { pack ->
                                savePackInRealm(pack)
                                packs.add(pack)
                            }
                        } catch (e: Exception) {
                            Log.e("error", "error parsing pack")
                        }
                    }
                    sharedPrefs.edit().apply {
                        putBoolean(SERVER_PACKS, false)
                        apply()
                    }
                    latch.countDown()

                }.addOnFailureListener {
                    latch.countDown()
                }

            withContext(Dispatchers.IO) { latch.await() }
            return packs

        } else {
            return realmDatabase.getObjectsFromRealm { query<PackDTO>().find() }
        }
    }

    private fun savePackInRealm(pack: PackDTO) {
        try {
            realmDatabase.addObject { pack }
        } catch (e: Exception) {
            println("Error writing in realm")
        }
    }

    override suspend fun getDataSelectedPack(gameType: String): Pair<String?, String?> {
        val packs = getPacks()
        when (gameType) {
            QUESTIONS_TYPE -> {
                val selected = getSelectedQPackId()
                val p = packs.find { it.packType == QUESTIONS && it.packNumber.toInt() == selected }
                return Pair(p?.icon, p?.title)
            }

            RANGES_TYPE -> {
                val selected = getSelectedRPackId()
                val p = packs.find { it.packType == RANGES && it.packNumber.toInt() == selected }
                return Pair(p?.icon, p?.title)
            }

            else -> return Pair(null, null)
        }
    }

    override suspend fun getQuestionsPack(): List<QuestionsPack> {
//        insertDataInFirestore()
//        return emptyList()
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
                "packId" to "pack_questions_free",
                "packNumber" to 0,
                "packType" to "questions",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Fhappy.png?alt=media&token=c9e91343-8be3-49bc-b6f3-693b68d83fbc",
                "title" to "Paquete básico",
                "description" to "Perfecto para demostrar cuánto conoces a tus amigos, familiares y personas cercanas.",
            ),
            hashMapOf(
                "packId" to "pack_questions_love",
                "packNumber" to 1,
                "packType" to "questions",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Fheart.png?alt=media&token=e6104f02-b24a-4bc2-b6a2-992fdcb5ada5",
                "title" to "Para parejas",
                "description" to "Enamoramiento, celos, romanticismo y situaciones íntimas para ver cuánto os conocéis en los sentimental. No vale discutir.",
            ),
            hashMapOf(
                "packId" to "pack_questions_more_1",
                "packNumber" to 2,
                "packType" to "questions",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Falien.png?alt=media&token=1702521d-45b7-4fe3-85c8-088f16ae47fe",
                "title" to "Más preguntas",
                "description" to "¿Quieres más? Con este pack vas a ver de verdad cómo son tus amigos. Ideal para reírte, sorprenderte y conocerlos mejor que nunca.",
            ),
            hashMapOf(
                "packId" to "pack_questions_more_2",
                "packNumber" to 2,
                "packType" to "questions",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Fstar.png?alt=media&token=1be5260e-7b5a-415c-a1d6-83e42a1f2142",
                "title" to "Todavía más preguntas",
                "description" to "Si pensabas que ya os habíais exprimido al máximo, aquí hay otras 50 preguntas diferentes para que sigáis dándole al coco y descubriendo cómo de diferente pensáis sobre vosotros mismos.",
            ),
            hashMapOf(
                "packId" to "pack_ranges_free",
                "packNumber" to 0,
                "packType" to "ranges",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Fone-finger.png?alt=media&token=8051782b-de69-4aa5-bb40-79fc5d3658f1",
                "title" to "Paquete básico",
                "description" to "Pack básico de rangos para que ponerte a prueba con tus compañer@s.",
            ),
            hashMapOf(
                "packId" to "pack_ranges_1",
                "packNumber" to 1,
                "packType" to "ranges",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Ftwo-fingers.png?alt=media&token=b3fda56e-fe31-45e6-b240-318183416903",
                "title" to "Tostadora 3000",
                "description" to "Aquí lo importante no es cómo piensas, si no cómo creen que piensas.",
            ),
            hashMapOf(
                "packId" to "pack_ranges_2",
                "packNumber" to 2,
                "packType" to "ranges",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Fthree-fingers.png?alt=media&token=edff5f03-210e-4b39-94a3-1f53e247e454",
                "title" to "Don Trapito",
                "description" to "No le des muchas vueltas, a veces es más simple de lo que parece.",
            ),
            hashMapOf(
                "packId" to "pack_ranges_3",
                "packNumber" to 3,
                "packType" to "ranges",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Ffour-fingers.png?alt=media&token=139dd119-0215-444a-846b-5ca3b2bd5c39",
                "title" to "Relojito tardón",
                "description" to "Tú sigue intentandolo que seguro que algo aciertas.",
            ),
        )
    }
}