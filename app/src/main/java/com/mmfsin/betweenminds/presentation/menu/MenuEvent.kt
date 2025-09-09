package com.mmfsin.betweenminds.presentation.menu

sealed class MenuEvent {
    data object SomethingWentWrong : MenuEvent()
}