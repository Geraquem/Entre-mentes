package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.repository.OfflineRepository
import com.mmfsin.betweenminds.domain.interfaces.IOfflineRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface OfflineRepositoryModule {
    @Binds
    fun bind(repository: OfflineRepository): IOfflineRepository
}