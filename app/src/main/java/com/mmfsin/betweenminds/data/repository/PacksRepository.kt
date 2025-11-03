package com.mmfsin.betweenminds.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mmfsin.betweenminds.data.mappers.createQuestionsPacks
import com.mmfsin.betweenminds.data.mappers.createRangesPacks
import com.mmfsin.betweenminds.data.mappers.toQuestionPack
import com.mmfsin.betweenminds.data.mappers.toRangesPack
import com.mmfsin.betweenminds.data.models.PackDTO
import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import com.mmfsin.betweenminds.domain.interfaces.IRealmDatabase
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.QuestionsPack
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.RangesPack
import com.mmfsin.betweenminds.utils.PACKS
import com.mmfsin.betweenminds.utils.PACK_ID
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

//        if (true) {
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

    private suspend fun insertPacksInBBDD() = insertDataInFirestore()

    override suspend fun getPackQuestions(packId: String): List<Question> {
        val pack = realmDatabase.getObjectFromRealm(PackDTO::class, PACK_ID, packId)
        return pack?.let { p ->
            p.toQuestionPack().questions.filter { it.pack == p.packNumber.toInt() }
        } ?: run { emptyList() }
    }

    override suspend fun getPackRanges(packId: String): List<Range> {
        val pack = realmDatabase.getObjectFromRealm(PackDTO::class, PACK_ID, packId)
        return pack?.let { p ->
            p.toRangesPack().ranges.filter { it.pack == p.packNumber.toInt() }
        } ?: run { emptyList() }
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
//        insertPacksInBBDD()
//        val packs = emptyList<PackDTO>()

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
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Fhappy.png?alt=media&token=c9e91343-8be3-49bc-b6f3-693b68d83fbc",
                "title" to "Básico",
                "description" to "Perfecto para demostrar cuánto conoces a tus amigos, familiares y personas cercanas.",
            ),
            hashMapOf(
                "packId" to "questions_pack_couples",
                "packNumber" to 1,
                "packType" to "questions",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Fheart.png?alt=media&token=e6104f02-b24a-4bc2-b6a2-992fdcb5ada5",
                "title" to "Para parejas",
                "description" to "Enamoramiento, celos, romanticismo y situaciones íntimas que revelan cómo pensáis y os sentís estando juntos. No vale discutir.",
            ),
            hashMapOf(
                "packId" to "questions_pack_more",
                "packNumber" to 2,
                "packType" to "questions",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Fmonkey.png?alt=media&token=6c5f8a1e-23b0-4f86-aa98-e2be00768480",
                "title" to "Más preguntas",
                "description" to "¿Quieres más? Con este pack vas a ver de verdad cómo son tus amigos: sus manías, reacciones y esos detalles que nunca muestran. Ideal para reírte, sorprenderte y conocerlos mejor que nunca.",
            ),
            hashMapOf(
                "packId" to "questions_pack_more_two",
                "packNumber" to 2,
                "packType" to "questions",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Ffox.png?alt=media&token=68b8c50d-0d96-409b-bb4a-52605eb11cc4",
                "title" to "Todavía más preguntas",
                "description" to "Si pensabas que ya os habíais exprimido al máximo, aquí hay otras 50 preguntas diferentes para que sigáis dándole al coco y descubriendo cómo de diferente pensáis sobre vosotros mismos.",
            ),
            hashMapOf(
                "packId" to "ranges_pack_free",
                "packNumber" to 0,
                "packType" to "ranges",
                "icon" to "https://firebasestorage.googleapis.com/v0/b/entre-mentes.firebasestorage.app/o/Packs%2Franges.png?alt=media&token=c4ba875c-7de5-4653-bcb5-372059967a9c",
                "title" to "Básico",
                "description" to "LAdklskdñldkfñlkslñfk ñlkfñls kdñ l",
            ),
            hashMapOf(
                "packId" to "ranges_pack_idk1",
                "packNumber" to 1,
                "packType" to "ranges",
                "icon" to "image_url",
                "title" to "No sé uno mirar wavelenght",
                "description" to "alkjdksjd lkajs dlklkdaslkjdlkas jljslj laj sljd lkasjdljweoigpor ig",
            ),
            hashMapOf(
                "packId" to "ranges_pack_idk2",
                "packNumber" to 2,
                "packType" to "ranges",
                "icon" to "image_url",
                "title" to "Mirar Wavelenght 2",
                "description" to "a´ldksañdkj ñlkdjf ñlkjsdlñ fjsdl lkñsdj lñksdj lkjfñdlsj ñlsj dñlkjgñlksj gñl",
            ),
        )
    }
}