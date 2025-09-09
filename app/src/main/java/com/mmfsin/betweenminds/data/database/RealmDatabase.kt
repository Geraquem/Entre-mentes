package com.mmfsin.betweenminds.data.database

import com.mmfsin.betweenminds.domain.interfaces.IRealmDatabase
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import kotlin.reflect.KClass

class RealmDatabase(private val realm: Realm) : IRealmDatabase {

    override fun deleteAllData() {
        realm.writeBlocking { deleteAll() }
    }

    override fun <T : RealmObject> getObjectsFromRealm(action: Realm.() -> RealmResults<T>): List<T> {
        val results = action(realm)
        return results.toList()
    }

    override fun <T : RealmObject> addObject(action: () -> T) {
        val obj = action()
        realm.writeBlocking { copyToRealm(obj) }
    }

    override fun <T : RealmObject> getObjectFromRealm(
        model: KClass<T>,
        idName: String,
        id: String
    ): T? {
        return realm.query(model, "$idName == $0", id).first().find()
    }

    override fun <T : RealmObject> deleteObject(model: KClass<T>, idName: String, id: String) {
        realm.writeBlocking {
            val obj = query(model, "$idName == $0", id).first().find()
            if (obj != null) delete(obj)
        }
    }

    override fun <T : RealmObject> deleteAllObjects(model: KClass<T>) {
        realm.writeBlocking {
            val objects = query(model).find()
            delete(objects)
        }
    }

    override suspend fun <R> write(block: MutableRealm.() -> R): R {
        return realm.write(block)
    }

    override fun <R> writeBlocking(block: MutableRealm.() -> R): R {
        return realm.writeBlocking(block)
    }
}