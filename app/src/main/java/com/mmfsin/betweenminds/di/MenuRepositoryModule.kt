package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.repository.MenuRepository
import com.mmfsin.betweenminds.domain.interfaces.IMenuRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface MenuRepositoryModule {
    @Binds
    fun bind(repository: MenuRepository): IMenuRepository
}