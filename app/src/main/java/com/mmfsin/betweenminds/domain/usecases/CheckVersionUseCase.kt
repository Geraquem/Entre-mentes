package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IMenuRepository
import javax.inject.Inject

class CheckVersionUseCase @Inject constructor(private val repository: IMenuRepository) {
    suspend fun execute() = repository.checkVersion()
}