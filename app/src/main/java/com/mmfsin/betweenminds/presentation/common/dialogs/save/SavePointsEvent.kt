package com.mmfsin.betweenminds.presentation.common.dialogs.save

sealed class SavePointsEvent {
    data object Completed : SavePointsEvent()
    data object SomethingWentWrong : SavePointsEvent()
}