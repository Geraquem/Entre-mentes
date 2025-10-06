package com.mmfsin.betweenminds.domain.interfaces

interface IOnlineRepository {
    suspend fun createRoom(userName: String): String?
}