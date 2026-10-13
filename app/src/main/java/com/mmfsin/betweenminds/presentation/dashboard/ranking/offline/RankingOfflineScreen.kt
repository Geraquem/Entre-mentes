package com.mmfsin.betweenminds.presentation.dashboard.ranking.offline

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmfsin.betweenminds.domain.models.GameType.RANKING
import com.mmfsin.betweenminds.domain.models.RankingBox
import com.mmfsin.betweenminds.domain.models.RankingPhaseType.NEXT_ROUND
import com.mmfsin.betweenminds.domain.models.RankingPhaseType.ORDER_FIRST
import com.mmfsin.betweenminds.domain.models.RankingPhaseType.ORDER_SECOND
import com.mmfsin.betweenminds.domain.models.RankingPhaseType.RESULTS
import com.mmfsin.betweenminds.presentation.core.components.BigText
import com.mmfsin.betweenminds.presentation.core.components.ButtonCustom
import com.mmfsin.betweenminds.presentation.core.components.CustomToolbar
import com.mmfsin.betweenminds.presentation.core.components.ErrorDialog
import com.mmfsin.betweenminds.presentation.core.components.MediumText
import com.mmfsin.betweenminds.presentation.core.components.SpacerLarge
import com.mmfsin.betweenminds.presentation.core.components.SpacerMedium
import com.mmfsin.betweenminds.presentation.core.components.SpacerSmall
import com.mmfsin.betweenminds.presentation.core.theme.BackgroundBlack
import com.mmfsin.betweenminds.presentation.core.theme.GrayHard
import com.mmfsin.betweenminds.presentation.core.theme.White
import com.mmfsin.betweenminds.presentation.core.theme.alphazet
import com.mmfsin.betweenminds.presentation.core.theme.courier
import com.mmfsin.betweenminds.presentation.dashboard.common.ExitGameDialog
import com.mmfsin.betweenminds.presentation.dashboard.common.RoundCount
import com.mmfsin.betweenminds.presentation.dashboard.ranking.components.BetweenPhasesDialog
import com.mmfsin.betweenminds.presentation.dashboard.ranking.components.DraggableOption
import com.mmfsin.betweenminds.presentation.dashboard.ranking.components.RankingRounds
import com.mmfsin.betweenminds.presentation.dashboard.ranking.helper.checkBoxColor
import com.mmfsin.betweenminds.presentation.dashboard.ranking.offline.components.InitialOfflineRankingDialog
import com.mmfsin.betweenminds.presentation.dashboard.ranking.offline.components.ResultRankingOfflineDialog
import com.mmfsin.betweenminds.utils.NAV_INSTR_RANKING_OFFLINE
import com.mmfsin.betweenminds.utils.ShowAlpha
import com.mmfsin.betweenminds.utils.getKonfettiParty
import com.mmfsin.betweenminds.utils.openBedRockActivity
import com.mmfsin.betweenminds.utils.shakeItem
import nl.dionsegijn.konfetti.compose.KonfettiView
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Preview
@Composable
fun RankingOfflineScreenPV() {
    RankingOfflineContent(
        uiStates = RankingOfflineStates(
            isLoading = true,
            showInitialDialog = false,
            showRoundView = false,
            actualRankingText = "El mejor superpoder",
            rankingBoxList = mutableListOf(RankingBox(0, "Item 1"), RankingBox(1, "Item 2")),
            actualRankings = mutableListOf("Desayuno", "Comida ", "Merienda", "Cena"),

            showComparativeList = true,
            secondSortedList = mutableListOf(RankingBox(0, "Item 1"), RankingBox(1, "Item 2")),
        ),
        {}, {}, {}, { _, _ -> },
        {}, {}, {}, {},
        {}, {}, {},
    )
}

@Composable
fun RankingOfflineScreen(viewModel: RankingOfflineViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val uiStates by viewModel.uiState.collectAsStateWithLifecycle()

    RankingOfflineContent(
        uiStates = uiStates,
        goBack = { activity?.finish() },
        goToInstructions = { context.goToInstructions() },
        hideInitialDialog = { viewModel.hideInitialDialog() },
        swapTexts = { targetIndex, sourceIndex -> viewModel.swapTexts(targetIndex, sourceIndex) },
        readyOrderOne = { viewModel.readyOrderOne() },
        showBetweenPhases = { viewModel.showBetweenPhases(it) },
        readyOrderTwo = { viewModel.readyOrderTwo() },
        handleNextRound = { viewModel.handleNextRound() },
        showResultDialog = { viewModel.showResultDialog(it) },
        replay = { viewModel.replay() },
        showExitDialog = { viewModel.showExitDialog(it) }
    )
}

@Composable
fun RankingOfflineContent(
    uiStates: RankingOfflineStates,
    goBack: () -> Unit,
    goToInstructions: () -> Unit,
    hideInitialDialog: () -> Unit,
    swapTexts: (Int, Int) -> Unit,
    readyOrderOne: () -> Unit,
    showBetweenPhases: (Boolean) -> Unit,
    readyOrderTwo: () -> Unit,
    handleNextRound: () -> Unit,
    showResultDialog: (Boolean) -> Unit,
    replay: () -> Unit,
    showExitDialog: (Boolean) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    val reorderableState = rememberReorderableLazyListState(
        lazyListState = lazyListState
    ) { from, to ->
        uiStates.rankingBoxList.add(to.index, uiStates.rankingBoxList.removeAt(from.index))
    }

    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragOptionIndex by remember { mutableIntStateOf(-1) }

    val boxBounds = remember { mutableStateMapOf<Int, Rect>() }
    val sourceBounds = remember { mutableStateMapOf<Int, Rect>() }
    var targetIndex by remember { mutableIntStateOf(-1) }

    Scaffold(
        topBar = {
            CustomToolbar(
                goBack = { goBack() },
                goToInstructions = { goToInstructions() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .background(BackgroundBlack)
                .padding(innerPadding)
                .padding(horizontal = 18.dp)
                .padding(bottom = 12.dp)
        ) {
            Column {

                RankingRounds(uiStates.points)

                SpacerLarge()

                MediumText(
                    text = uiStates.actualRankingText,
                    color = White,
                    fontFamily = courier,
                    gravity = TextAlign.Center,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                SpacerMedium()

                LazyColumn(
                    modifier = Modifier.shakeItem(uiStates.shakeTrigger),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = uiStates.rankingBoxList,
                        key = { _, item -> item.id }
                    ) { index, item ->

                        val originalPosition = uiStates.firstSortedList.indexOfFirst { it.text == item.text }

                        ReorderableItem(
                            state = reorderableState,
                            key = item.id
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BigText(
                                    text = "${index + 1}º",
                                    modifier = Modifier.width(26.dp),
                                    gravity = TextAlign.End,
                                    color = White,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = alphazet
                                )

                                SpacerSmall(horizontal = true)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            boxBounds[index] = coordinates.boundsInRoot()
                                        }
                                        .background(
                                            color = if (uiStates.showComparativeList) {
                                                checkBoxColor(
                                                    firstText = uiStates.firstSortedList[index].text,
                                                    secondText = uiStates.secondSortedList[index].text
                                                )
                                            } else GrayHard,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(16.dp)
                                        .draggableHandle(enabled = uiStates.dragEnabled)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        MediumText(
                                            text = item.text,
                                            color = White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (uiStates.showComparativeList) {
                                            SpacerSmall(horizontal = true)
                                            MediumText(
                                                text = "${originalPosition + 1}º",
                                                color = White,
                                                fontSize = 18.sp,
                                                fontFamily = alphazet,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                SpacerLarge()

                Column(Modifier.fillMaxWidth().weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                            DraggableOption(
                                text = uiStates.actualRankings[0],
                                index = 0,
                                sourceBounds = sourceBounds,
                                sourceBoundsIndex = 0,
                                updateBounds = { bounds -> sourceBounds[0] = bounds },
                                boxBounds = boxBounds,
                                draggedIndex = dragOptionIndex,
                                updateDraggedIndex = { dragOptionIndex = it },
                                dragOffset = dragOffset,
                                updateDragOffset = { if (it == Offset.Zero) dragOffset = it else dragOffset += it },
                                updateTargetIndex = { targetIndex = it },
                                swapTexts = { swapTexts(targetIndex, 0) }
                            )
                        }

                        SpacerSmall(horizontal = true)

                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            DraggableOption(
                                text = uiStates.actualRankings[1],
                                index = 1,
                                sourceBounds = sourceBounds,
                                sourceBoundsIndex = 1,
                                updateBounds = { bounds -> sourceBounds[1] = bounds },
                                boxBounds = boxBounds,
                                draggedIndex = dragOptionIndex,
                                updateDraggedIndex = { dragOptionIndex = it },
                                dragOffset = dragOffset,
                                updateDragOffset = { if (it == Offset.Zero) dragOffset = it else dragOffset += it },
                                updateTargetIndex = { targetIndex = it },
                                swapTexts = { swapTexts(targetIndex, 1) }
                            )
                        }
                    }

                    SpacerSmall()

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                            DraggableOption(
                                text = uiStates.actualRankings[2],
                                index = 2,
                                sourceBounds = sourceBounds,
                                sourceBoundsIndex = 2,
                                updateBounds = { bounds -> sourceBounds[2] = bounds },
                                boxBounds = boxBounds,
                                draggedIndex = dragOptionIndex,
                                updateDraggedIndex = { dragOptionIndex = it },
                                dragOffset = dragOffset,
                                updateDragOffset = { if (it == Offset.Zero) dragOffset = it else dragOffset += it },
                                updateTargetIndex = { targetIndex = it },
                                swapTexts = { swapTexts(targetIndex, 2) }
                            )
                        }

                        SpacerSmall(horizontal = true)

                        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                            DraggableOption(
                                text = uiStates.actualRankings[3],
                                index = 3,
                                sourceBounds = sourceBounds,
                                sourceBoundsIndex = 3,
                                updateBounds = { bounds -> sourceBounds[3] = bounds },
                                boxBounds = boxBounds,
                                draggedIndex = dragOptionIndex,
                                updateDraggedIndex = { dragOptionIndex = it },
                                dragOffset = dragOffset,
                                updateDragOffset = { if (it == Offset.Zero) dragOffset = it else dragOffset += it },
                                updateTargetIndex = { targetIndex = it },
                                swapTexts = { swapTexts(targetIndex, 3) }
                            )
                        }
                    }
                }

                SpacerLarge()

                ButtonCustom(
                    onClick = {
                        if (uiStates.buttonEnabled) {
                            when (uiStates.phase) {
                                ORDER_FIRST -> readyOrderOne()
                                ORDER_SECOND -> readyOrderTwo()
                                NEXT_ROUND -> handleNextRound()
                                RESULTS -> showResultDialog(true)
                            }
                        }
                    },
                    text = uiStates.buttonText,
                    modifier = Modifier.fillMaxWidth()
                )
                SpacerLarge()
            }

            if (uiStates.confettiTrigger > 0) {
                key(uiStates.confettiTrigger) {
                    KonfettiView(
                        modifier = Modifier.fillMaxSize(),
                        parties = listOf(getKonfettiParty(RANKING, uiStates.confettiTrigger))
                    )
                }
            }

            ShowAlpha(uiStates.showRoundView) { RoundCount(uiStates.roundCount) }

            if (uiStates.showInitialDialog) {
                InitialOfflineRankingDialog(
                    startGame = { hideInitialDialog() },
                    howToPlay = { goToInstructions() },
                    exit = { goBack() },
                    isLoading = uiStates.isLoading
                )
            }

            if (uiStates.showDialogBetweenPhases) {
                BetweenPhasesDialog(onEnd = { showBetweenPhases(false) })
            }

            if (uiStates.showResultDialog) {
                ResultRankingOfflineDialog(
                    points = uiStates.points,
                    exit = { goBack() },
                    replay = { replay() },
                )
            }

            if (uiStates.showExitDialog) {
                ExitGameDialog(
                    exit = { goBack() },
                    cancel = { showExitDialog(false) },
                )
            }

            if (uiStates.showSwwDialog) ErrorDialog(accept = { goBack() })

            BackHandler { showExitDialog(true) }
        }
    }
}

private fun Context.goToInstructions() = openBedRockActivity(NAV_INSTR_RANKING_OFFLINE)