package com.mmfsin.betweenminds.domain.interfaces

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import kotlin.reflect.KClass

interface IRealmDatabase {
    fun <T : RealmObject> getObjectsFromRealm(action: Realm.() -> RealmResults<T>): List<T>
    fun <T : RealmObject> addObject(action: () -> T)
    fun <T : RealmObject> getObjectFromRealm(model: KClass<T>, idName: String, id: String): T?
    fun <T : RealmObject> deleteObject(model: KClass<T>, idName: String, id: String)
    fun <T : RealmObject> deleteAllObjects(model: KClass<T>)
    fun deleteAllData()

    suspend fun <R> write(block: MutableRealm.() -> R): R
    fun <R> writeBlocking(block: MutableRealm.() -> R): R
}
