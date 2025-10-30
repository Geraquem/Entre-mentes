package com.mmfsin.betweenminds.data.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

open class RangeDTO : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var leftRange: String = ""
    var rightRange: String = ""
    var pack: Int = 0
}
