package com.mmfsin.betweenminds.presentation.question

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetPhrasesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val getPhrasesUseCase: GetPhrasesUseCase
) : BaseViewModel<QuestionEvent>() {

    fun getPhrases() {
        executeUseCase(
            { getPhrasesUseCase.execute() },
            { result -> _event.value = QuestionEvent.Phrases(result) },
            { _event.value = QuestionEvent.SomethingWentWrong }
        )
    }
}