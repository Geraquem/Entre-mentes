package com.mmfsin.betweenminds.presentation.packs

sealed class PacksEvent {
    data object SomethingWentWrong : PacksEvent()
}