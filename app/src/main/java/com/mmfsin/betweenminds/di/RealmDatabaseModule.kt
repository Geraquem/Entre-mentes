package com.mmfsin.betweenminds.di

import com.mmfsin.betweenminds.data.database.RealmDatabase
import com.mmfsin.betweenminds.data.models.QuestionDTO
import com.mmfsin.betweenminds.data.models.SavedScoreDTO
import com.mmfsin.betweenminds.domain.interfaces.IRealmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class)
object RealmDatabaseModule {

    @Provides
    fun provideRealmDatabase(): IRealmDatabase {
        val config = RealmConfiguration.create(
            schema = setOf(
                SavedScoreDTO::class,
                QuestionDTO::class
            )
        )

        val realm = Realm.open(config)
        return RealmDatabase(realm)
    }
}