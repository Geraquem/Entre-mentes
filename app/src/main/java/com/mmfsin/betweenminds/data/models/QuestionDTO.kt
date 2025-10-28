package com.mmfsin.betweenminds.data.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

open class QuestionDTO : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var question: String = ""
    var pack: Long = 0
}
