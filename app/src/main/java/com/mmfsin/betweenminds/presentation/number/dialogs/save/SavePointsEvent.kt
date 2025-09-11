package com.mmfsin.betweenminds.presentation.number.dialogs.save

sealed class SavePointsEvent {
    data object Completed : SavePointsEvent()
    data object SomethingWentWrong : SavePointsEvent()
}