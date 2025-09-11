package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.repository.ScoreRepository
import com.mmfsin.betweenminds.domain.interfaces.IScoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ScoreRepositoryModule {
    @Binds
    fun bind(repository: ScoreRepository): IScoreRepository
}