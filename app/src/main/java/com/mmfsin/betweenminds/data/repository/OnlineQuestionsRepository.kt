package com.mmfsin.betweenminds.data.repository

import android.content.Context
import com.mmfsin.betweenminds.domain.interfaces.IOnlineQuestionsRepository
import com.mmfsin.betweenminds.domain.models.Question
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OnlineQuestionsRepository @Inject constructor(
    @ApplicationContext val context: Context,
) : IOnlineQuestionsRepository {

    override suspend fun setQuestionsInRoom(
        roomId: String,
        names: Pair<String, String>,
        questions: List<Question>
    ) {

    }
}