package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.repository.OnlineQuestionsRepository
import com.mmfsin.betweenminds.domain.interfaces.IOnlineQuestionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface OnlineQuestionsRepositoryModule {
    @Binds
    fun bind(repository: OnlineQuestionsRepository): IOnlineQuestionsRepository
}