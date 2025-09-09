package com.mmfsin.betweenminds.presentation.number

sealed class NumberEvent {
    data object SomethingWentWrong : NumberEvent()
}