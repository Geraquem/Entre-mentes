package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.repository.OnlineRoomRepository
import com.mmfsin.betweenminds.domain.interfaces.IOnlineRoomRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface OnlineRoomRepositoryModule {
    @Binds
    fun bind(repository: OnlineRoomRepository): IOnlineRoomRepository
}