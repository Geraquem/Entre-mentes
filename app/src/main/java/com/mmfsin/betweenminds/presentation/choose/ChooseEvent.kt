package com.mmfsin.betweenminds.presentation.choose

sealed class ChooseEvent {
    data class RoomCreated(val roomId: String) : ChooseEvent()
    data object SomethingWentWrong : ChooseEvent()
}