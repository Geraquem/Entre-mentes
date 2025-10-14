package com.mmfsin.betweenminds.domain.interfaces

interface IOnlineRoomRepository {
    suspend fun createRoom(): String?
    suspend fun joinRoom(roomId: String): Boolean
    suspend fun waitToJoinRoom(roomId: String)

    suspend fun restartGameAndResetRoom(roomId: String)
}