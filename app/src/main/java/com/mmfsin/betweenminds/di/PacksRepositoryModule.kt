package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.repository.PacksRepository
import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface PacksRepositoryModule {
    @Binds
    fun bind(repository: PacksRepository): IPacksRepository
}