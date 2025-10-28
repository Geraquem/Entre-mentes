package com.mmfsin.betweenminds.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import com.mmfsin.betweenminds.utils.QUESTIONS_PACK
import com.mmfsin.betweenminds.utils.RANGES_PACK
import com.mmfsin.betweenminds.utils.SHARED_PREFS
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PacksRepository @Inject constructor(
    @ApplicationContext val context: Context
) : IPacksRepository {

    override fun getQuestionsPackId(): Int {
        return getSharedPreferences().getInt(QUESTIONS_PACK, 0)
    }

    override fun editQuestionsPackId(packId: Int) {
        val editor = getSharedPreferences().edit()
        editor.putInt(QUESTIONS_PACK, packId)
        editor.apply()
    }

    override fun getRangesPackId(): Int {
        return getSharedPreferences().getInt(RANGES_PACK, 0)
    }

    override fun editRangesPackId(packId: Int) {
        val editor = getSharedPreferences().edit()
        editor.putInt(RANGES_PACK, packId)
        editor.apply()
    }

    private fun getSharedPreferences() = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
}