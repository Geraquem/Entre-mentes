package com.mmfsin.betweenminds.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val question: String,
    val pack: Int
) : Parcelable
