package com.mmfsin.betweenminds.presentation.question

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetQuestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val getQuestionsUseCase: GetQuestionsUseCase
) : BaseViewModel<QuestionEvent>() {

    fun getQuestions() {
        executeUseCase(
            { getQuestionsUseCase.execute() },
            { result -> _event.value = QuestionEvent.Questions(result) },
            { _event.value = QuestionEvent.SomethingWentWrong }
        )
    }
}