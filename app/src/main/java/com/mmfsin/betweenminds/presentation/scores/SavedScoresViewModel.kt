package com.mmfsin.betweenminds.presentation.scores

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetSavedScoresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedScoresViewModel @Inject constructor(
    private val getSavedScoresUseCase: GetSavedScoresUseCase
) : BaseViewModel<SavedScoresEvent>() {

    fun getSavedScores() {
        executeUseCase(
            { getSavedScoresUseCase.execute() },
            { result -> _event.value = SavedScoresEvent.Scores(result) },
            { _event.value = SavedScoresEvent.SomethingWentWrong }
        )
    }
}