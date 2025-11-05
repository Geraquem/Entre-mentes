package com.mmfsin.betweenminds.presentation.packs.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedManager @Inject constructor(defaultPackId: Int?) {

    private val _selectedQuestionPackNumber = MutableLiveData<Int?>().apply {
        value = defaultPackId
    }

    private val _selectedRangesPackNumber = MutableLiveData<Int?>().apply {
        value = defaultPackId
    }

    val selectedQuestionPackNumber: LiveData<Int?> = _selectedQuestionPackNumber
    val selectedRangesPackNumber: LiveData<Int?> = _selectedRangesPackNumber

    fun updateSelectedQuestionPackNumber(packNumber: Int?) {
        _selectedQuestionPackNumber.value = packNumber
    }

    fun updateSelectedRangesPackNumber(packNumber: Int?) {
        _selectedRangesPackNumber.value = packNumber
    }
}
