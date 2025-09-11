package com.mmfsin.betweenminds.data.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class PhraseDTO : RealmObject {
    @PrimaryKey
    var id: String = ""
    var text: String = ""
}
