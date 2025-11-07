package com.mmfsin.betweenminds.presentation.packs

sealed class PacksVPEvent {
    data class FreePack(val areFree: Boolean) : PacksVPEvent()
}