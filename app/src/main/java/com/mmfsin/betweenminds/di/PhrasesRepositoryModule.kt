package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.repository.PhrasesRepository
import com.mmfsin.betweenminds.domain.interfaces.IPhrasesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface PhrasesRepositoryModule {
    @Binds
    fun bind(repository: PhrasesRepository): IPhrasesRepository
}