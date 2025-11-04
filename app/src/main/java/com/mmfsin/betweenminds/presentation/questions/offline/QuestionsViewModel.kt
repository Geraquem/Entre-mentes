package com.mmfsin.betweenminds.presentation.questions.offline

import com.mmfsin.betweenminds.base.BaseViewModel
import com.mmfsin.betweenminds.domain.usecases.GetQuestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val getQuestionsUseCase: GetQuestionsUseCase
) : BaseViewModel<QuestionsEvent>() {

    fun getQuestions() {
        executeUseCase(
            { getQuestionsUseCase.execute() },
            { result ->
                _event.value =
                    if (result.isEmpty()) QuestionsEvent.SomethingWentWrong
                    else QuestionsEvent.Questions(result)
            },
            { _event.value = QuestionsEvent.SomethingWentWrong }
        )
    }
}