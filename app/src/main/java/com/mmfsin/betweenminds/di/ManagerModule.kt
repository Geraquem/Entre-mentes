package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.presentation.packs.manager.SelectedManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {
    @Provides
    @Singleton
    fun selectedManager(): SelectedManager {
        return SelectedManager(null)
    }
}