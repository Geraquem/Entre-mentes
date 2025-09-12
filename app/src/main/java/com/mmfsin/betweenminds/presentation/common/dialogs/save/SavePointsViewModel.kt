package com.mmfsin.betweenminds.presentation.common.dialogs.save

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.models.SavedScore
import com.mmfsin.betweenminds.domain.usecases.SaveScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavePointsViewModel @Inject constructor(
    private val saveScoreUseCase: SaveScoreUseCase
) : BaseViewModel<SavePointsEvent>() {

    fun saveNewScore(score: SavedScore) {
        executeUseCase(
            { saveScoreUseCase.execute(score) },
            { _event.value = SavePointsEvent.Completed },
            { _event.value = SavePointsEvent.SomethingWentWrong }
        )
    }
}