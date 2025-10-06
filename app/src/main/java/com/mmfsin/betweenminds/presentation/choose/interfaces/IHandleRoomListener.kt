package com.mmfsin.betweenminds.presentation.choose.interfaces

interface IHandleRoomListener {
    fun createRoom(userName: String)
    fun joinRoom(roomCode: String, userName: String)
}