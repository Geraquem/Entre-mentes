package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.repository.OnlineRepository
import com.mmfsin.betweenminds.domain.interfaces.IOnlineRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface OnlineRepositoryModule {
    @Binds
    fun bind(repository: OnlineRepository): IOnlineRepository
}