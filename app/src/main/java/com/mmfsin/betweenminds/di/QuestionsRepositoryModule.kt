package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.repository.QuestionsRepository
import com.mmfsin.betweenminds.domain.interfaces.IQuestionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface QuestionsRepositoryModule {
    @Binds
    fun bind(repository: QuestionsRepository): IQuestionsRepository
}