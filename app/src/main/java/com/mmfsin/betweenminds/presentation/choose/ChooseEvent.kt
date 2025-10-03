package com.mmfsin.betweenminds.presentation.choose

sealed class ChooseEvent {
    data object SomethingWentWrong : ChooseEvent()
}