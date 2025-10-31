package com.mmfsin.betweenminds.presentation.choose

sealed class ChooseEvent {
    data class SelectedPack(var data: Pair<String?, String?>) : ChooseEvent()
    data class RoomCreated(var roomId: String?) : ChooseEvent()
    data class JoinedToRoom(val joined: Boolean, val roomId: String) : ChooseEvent()
    data object SomethingWentWrong : ChooseEvent()
}