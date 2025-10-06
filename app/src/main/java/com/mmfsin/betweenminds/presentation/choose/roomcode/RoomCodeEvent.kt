package com.mmfsin.betweenminds.presentation.choose.roomcode

sealed class RoomCodeEvent {
    data object ListeningOtherPlayer : RoomCodeEvent()
    data object SomethingWentWrong : RoomCodeEvent()
}