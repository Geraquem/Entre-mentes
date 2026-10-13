package com.mmfsin.betweenminds.presentation.dashboard.ranking.offline

import androidx.lifecycle.viewModelScope
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.domain.models.RankingPhaseType.NEXT_ROUND
import com.mmfsin.betweenminds.domain.models.RankingPhaseType.ORDER_FIRST
import com.mmfsin.betweenminds.domain.models.RankingPhaseType.ORDER_SECOND
import com.mmfsin.betweenminds.domain.models.RankingPhaseType.RESULTS
import com.mmfsin.betweenminds.domain.models.emptyRankingBoxList
import com.mmfsin.betweenminds.domain.usecases.GetRankingDataUseCase
import com.mmfsin.betweenminds.presentation.core.base.BaseViewModel
import com.mmfsin.betweenminds.presentation.dashboard.ranking.helper.calculatePoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingOfflineViewModel @Inject constructor(
    private val getRankingDataUseCase: GetRankingDataUseCase
) : BaseViewModel<RankingOfflineStates>(RankingOfflineStates()) {

    init {
        getRankings()
    }

    fun getRankings() {
        executeUseCase(
            { getRankingDataUseCase() },
            { rankings ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rankings = rankings
                    )
                }

                setRanking()
            },
            { sww() }
        )
    }

    private fun setRanking() {
        val states = uiState.value
        val rankings = states.rankings

        if (rankings.isEmpty()) sww()
        else {
            val newRanking = if (states.rankingPos >= states.rankings.size) {
                _uiState.update { it.copy(rankingPos = 0) }
                rankings[0]
            } else rankings[states.rankingPos]

            _uiState.update {
                it.copy(
                    actualRankingText = newRanking.text,
                    actualRankings = newRanking.rankings.toMutableList(),
                    actualRankingsAux = newRanking.rankings.toMutableList()
                )
            }
        }
    }

    fun hideInitialDialog() {
        _uiState.update { it.copy(showInitialDialog = false) }
        viewModelScope.launch {
            delay(1000)
            _uiState.update { it.copy(showRoundView = false) }
        }
    }

    fun swapTexts(targetIndex: Int, sourceIndex: Int) {
        val states = uiState.value
        val boxItem = states.rankingBoxList[targetIndex]
        val optionItem = states.actualRankings[sourceIndex]

        if (optionItem != "") {
            states.rankingBoxList[targetIndex] = boxItem.copy(text = optionItem)
            states.actualRankings[sourceIndex] = boxItem.text
        }
    }

    fun readyOrderOne() {
        val states = uiState.value
        if (!(states.rankingBoxList.any { it.text.isEmpty() })) {
            _uiState.update {
                it.copy(
                    showDialogBetweenPhases = true,
                    phase = ORDER_SECOND,
                    buttonEnabled = false,
                    buttonText = R.string.btn_check,
                    firstSortedList = states.rankingBoxList,
                    actualRankings = states.actualRankingsAux,
                    rankingBoxList = emptyRankingBoxList(),
                    shakeVerticalTrigger = true
                )
            }

            viewModelScope.launch {
                delay(1000)
                _uiState.update {
                    it.copy(
                        buttonEnabled = true,
                    )
                }
            }
        }
    }

    fun showBetweenPhases(value: Boolean) = _uiState.update { it.copy(showDialogBetweenPhases = value) }

    fun readyOrderTwo() {
        val states = uiState.value
        if (!(states.rankingBoxList.any { it.text.isEmpty() })) {

            val roundPoints = calculatePoints(
                firstList = states.firstSortedList.map { it.text },
                secondList = states.rankingBoxList.map { it.text }
            )

            _uiState.update {
                it.copy(
                    buttonEnabled = false,
                    dragEnabled = false,

                    secondSortedList = states.rankingBoxList,

                    points = states.points.toMutableList().apply { this[states.roundCount] = roundPoints },
                    confettiTrigger = roundPoints,
                    shakeTrigger = roundPoints == 0,

                    showComparativeList = true,

                    shakeVerticalTrigger = false,
                    phase = if (states.roundCount != 3) NEXT_ROUND else RESULTS,

                    rankingPos = states.rankingPos + 1,
                    roundCount = states.roundCount + 1
                )
            }

            viewModelScope.launch {
                delay(1500)
                _uiState.update {
                    it.copy(
                        buttonEnabled = true,
                        buttonText = if (states.roundCount != 3) R.string.btn_next_round else R.string.btn_see_result
                    )
                }
            }
        }
    }

    fun handleNextRound() {
        _uiState.update {
            it.copy(
                showRoundView = true,
                buttonEnabled = false,
                dragEnabled = false,
            )
        }

        viewModelScope.launch {
            delay(1500)
            _uiState.update {
                it.copy(
                    showComparativeList = false,
                    confettiTrigger = 0,
                    rankingBoxList = emptyRankingBoxList(),
                    phase = ORDER_FIRST,
                    buttonText = R.string.btn_ready,
                    showRoundView = false,
                )
            }
            setRanking()

            delay(250)

            _uiState.update {
                it.copy(
                    buttonEnabled = true,
                    dragEnabled = true
                )
            }
        }
    }

    fun showResultDialog(value: Boolean) = _uiState.update { it.copy(showResultDialog = value) }

    fun replay() {
        _uiState.update {
            it.copy(
                showResultDialog = false,
                phase = ORDER_FIRST,
                roundCount = 0,
                showRoundView = true,
                points = listOf(null, null, null, null),
                buttonEnabled = false,
            )
        }

        viewModelScope.launch {
            delay(1500)
            _uiState.update {
                it.copy(
                    showRoundView = false,
                    actualRankingText = "",
                    showComparativeList = false,

                    actualRankings = listOf("", "", "", "").toMutableList(),
                    actualRankingsAux = listOf("", "", "", "").toMutableList(),

                    rankingBoxList = emptyRankingBoxList(),
                    firstSortedList = emptyRankingBoxList(),
                    secondSortedList = emptyRankingBoxList(),

                    dragEnabled = true,
                    buttonEnabled = true,
                    buttonText = R.string.btn_ready
                )
            }

            delay(1000)
            setRanking()
        }
    }

    fun showExitDialog(value: Boolean) = _uiState.update { it.copy(showExitDialog = value) }

    private fun sww() = _uiState.update { it.copy(showSwwDialog = true) }
}