package com.mmfsin.betweenminds.data.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class SavedScoreDTO : RealmObject {
    @PrimaryKey
    var id: String = ""
    var playerOneName: String = ""
    var playerTwoName: String = ""
    var points: Int = -1
    var notes: String = ""
    var date: String = ""
    var mode: String = ""
}
