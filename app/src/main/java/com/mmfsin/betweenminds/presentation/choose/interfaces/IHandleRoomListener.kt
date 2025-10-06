package com.mmfsin.betweenminds.presentation.choose.interfaces

interface IHandleRoomListener {
    fun createRoom(userName: String)
    fun joinRoom(userName: String, roomId: String)
}