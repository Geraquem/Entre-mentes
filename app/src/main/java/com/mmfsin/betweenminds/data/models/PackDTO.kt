package com.mmfsin.betweenminds.data.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class PackDTO : RealmObject {
    @PrimaryKey
    var packId: String = ""
    var packNumber: Long = 0
    var packType: String = ""
    var price: String = ""
    var icon: String = ""
    var title: String = ""
    var description: String = ""
}