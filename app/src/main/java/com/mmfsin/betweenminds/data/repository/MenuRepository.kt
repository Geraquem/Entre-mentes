package com.mmfsin.betweenminds.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mmfsin.betweenminds.domain.interfaces.IMenuRepository
import com.mmfsin.betweenminds.domain.interfaces.IRealmDatabase
import com.mmfsin.betweenminds.utils.SAVED_VERSION
import com.mmfsin.betweenminds.utils.SERVER_PACKS
import com.mmfsin.betweenminds.utils.SERVER_QUESTIONS
import com.mmfsin.betweenminds.utils.SERVER_RANGES
import com.mmfsin.betweenminds.utils.SHARED_PREFS
import com.mmfsin.betweenminds.utils.VERSION
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.coroutines.resume

class MenuRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val realmDatabase: IRealmDatabase
) : IMenuRepository {

    override suspend fun checkVersion() {
        getVersionFromFirebase(getSavedVersion())
    }

    private suspend fun getVersionFromFirebase(savedVersion: Long) {
        val fetchBlock: suspend () -> Unit = {
            suspendCancellableCoroutine { coroutine ->
                Firebase.database.reference.get().addOnSuccessListener {
                    val version = it.child(VERSION).value as Long
                    if (version != savedVersion) {
                        saveVersion(newVersion = version)
                        restartSystemData()
                    }
                    coroutine.resume(Unit)
                }
            }
        }

        try {
            if (savedVersion == -1L) fetchBlock()
            else {
                withTimeout(5000) {
                    fetchBlock()
                }
            }
        } catch (e: TimeoutCancellationException) {
            println("**** FirebaseTimeout **** -> Se agotó el tiempo de espera")
        } catch (e: Exception) {
            println("FirebaseError -> Error al obtener datos: ${e.message}")
        }
    }

    private fun saveVersion(newVersion: Long) {
        val editor = getSharedPreferences().edit()
        editor.putLong(SAVED_VERSION, newVersion)
        editor.apply()
    }

    private fun restartSystemData() {
        realmDatabase.deleteAllData()
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putBoolean(SERVER_RANGES, true)
            putBoolean(SERVER_QUESTIONS, true)
            putBoolean(SERVER_PACKS, true)
            apply()
        }
    }

    private fun getSavedVersion(): Long = getSharedPreferences().getLong(SAVED_VERSION, -1)
    private fun getSharedPreferences() = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
}