package com.mmfsin.betweenminds.presentation.online.ranges.joined

sealed class ORangesJoinedEvent {
    data object SomethingWentWrong : ORangesJoinedEvent()
}