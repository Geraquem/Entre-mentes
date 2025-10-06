package com.mmfsin.betweenminds.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class BaseViewModel<T> : ViewModel() {

    protected val _event by lazy { MutableLiveData<T>() }
    val event: LiveData<T> = _event

    fun <T> executeUseCase(
        useCase: suspend () -> T,
        success: (T) -> Unit,
        error: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                useCase().also { result -> success(result) }
            } catch (t: Throwable) {
                error(t)

                println("*****************************************************************")
                println("*****************************************************************")
                println("------------------------------ERROR------------------------------")
                println("${t.message}")
                println("-----------------------------------------------------------------")
                println("*****************************************************************")
                println("*****************************************************************")
            }
        }
    }
}