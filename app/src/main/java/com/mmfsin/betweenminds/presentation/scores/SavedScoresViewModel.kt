package com.mmfsin.betweenminds.presentation.scores

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.DeleteSavedScoreUseCase
import com.mmfsin.betweenminds.domain.usecases.GetSavedScoresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedScoresViewModel @Inject constructor(
    private val getSavedScoresUseCase: GetSavedScoresUseCase,
    private val deleteSavedScoreUseCase: DeleteSavedScoreUseCase
) : BaseViewModel<SavedScoresEvent>() {

    fun getSavedScores() {
        executeUseCase(
            { getSavedScoresUseCase.execute() },
            { result -> _event.value = SavedScoresEvent.Scores(result) },
            { _event.value = SavedScoresEvent.SomethingWentWrong }
        )
    }

    fun deleteSavedScore(savedId: String) {
        executeUseCase(
            { deleteSavedScoreUseCase.execute(savedId) },
            { _event.value = SavedScoresEvent.ScoreDeleted(savedId) },
            { _event.value = SavedScoresEvent.SomethingWentWrong }
        )
    }
}