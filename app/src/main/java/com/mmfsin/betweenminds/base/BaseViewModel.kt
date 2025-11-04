package com.mmfsin.betweenminds.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel<T> : ViewModel() {

    protected val _event by lazy { MutableLiveData<T>() }
    val event: LiveData<T> = _event

    fun <T> executeUseCase(
        useCase: suspend () -> T,
        success: (T) -> Unit,
        error: (Throwable) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = useCase()
                withContext(Dispatchers.Main) { success(result) }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) { error(t) }

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