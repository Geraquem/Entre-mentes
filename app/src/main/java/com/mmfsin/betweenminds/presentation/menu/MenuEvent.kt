package com.mmfsin.betweenminds.presentation.menu

sealed class MenuEvent {
    data object VersionCompleted : MenuEvent()
    data object FreePacks : MenuEvent()
    data object SomethingWentWrong : MenuEvent()
}