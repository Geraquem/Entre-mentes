package com.mmfsin.betweenminds.base

abstract class BaseUseCaseNoParams<T> {
    abstract suspend fun execute(): T
}