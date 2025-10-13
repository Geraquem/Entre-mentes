package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.repository.OnlineRangesRepository
import com.mmfsin.betweenminds.domain.interfaces.IOnlineRangesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface OnlineRangesRepositoryModule {
    @Binds
    fun bind(repository: OnlineRangesRepository): IOnlineRangesRepository
}