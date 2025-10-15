package com.mmfsin.betweenminds.domain.interfaces

interface IOnlineRoomRepository {
    suspend fun createRoom(gameType: String): String?
    suspend fun joinRoom(roomId: String, gameType: String): Boolean
    suspend fun waitToJoinRoom(roomId: String)

    suspend fun restartGameAndResetRoom(roomId: String)
}