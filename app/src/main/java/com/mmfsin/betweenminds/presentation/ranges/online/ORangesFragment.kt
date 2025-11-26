package com.mmfsin.betweenminds.presentation.ranges.online

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentRangesOnlineBinding
import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.models.OnlineRoundData
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.ScoreRange
import com.mmfsin.betweenminds.presentation.ranges.adapter.ScoreboardRangesAdapter
import com.mmfsin.betweenminds.presentation.ranges.dialogs.EndGameORangesDialog
import com.mmfsin.betweenminds.presentation.ranges.dialogs.ORangesStartDialog
import com.mmfsin.betweenminds.utils.BEDROCK_BOOLEAN_ARGS
import com.mmfsin.betweenminds.utils.BEDROCK_STR_ARGS
import com.mmfsin.betweenminds.utils.RANGES_TYPE
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.checkNotNulls
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyOScoreRangesList
import com.mmfsin.betweenminds.utils.getKonfettiParty
import com.mmfsin.betweenminds.utils.handleAlpha
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import com.mmfsin.betweenminds.utils.waitingPartnerVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ORangesFragment : BaseFragment<FragmentRangesOnlineBinding, ORangesViewModel>() {

    override val viewModel: ORangesViewModel by viewModels()

    private lateinit var mContext: Context

    var roomId: String? = null
    var isCreator: Boolean? = null

    private var rangesList: List<Range> = emptyList()
    private var position = 0
    private var bullseyePosition = 0f
    private var round = 1

    private val data = mutableListOf<OnlineRoundData>()

    private var otherPlayerData: List<OnlineRoundData> = emptyList()
    private var otherPlayerPosition = 0
    private var pointsObtained = 0

    private var scoreboardRangesAdapter: ScoreboardRangesAdapter? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRangesOnlineBinding.inflate(inflater, container, false)

    override fun getBundleArgs() {
        activity?.intent?.apply {
            roomId = getStringExtra(BEDROCK_STR_ARGS)
            isCreator = getBooleanExtra(BEDROCK_BOOLEAN_ARGS, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNotNulls(roomId, isCreator) { _, _ -> } ?: run { error() }

        activity?.showFragmentDialog(
            ORangesStartDialog(close = { activity?.onBackPressedDispatcher?.onBackPressed() },
                start = { viewModel.getRanges() },
                instructions = { openInstructions() })
        )
    }

    private fun setUpScoreboard() {
        binding.apply {
            scoreboard.rvScore.apply {
                layoutManager = GridLayoutManager(mContext, 3)
                scoreboardRangesAdapter = ScoreboardRangesAdapter(getEmptyOScoreRangesList())
                adapter = scoreboardRangesAdapter
            }
        }
    }

    override fun setUI() {
        binding.apply {
            loading.root.isVisible = true

            rlPartner.alpha = 0f
            waitingPartnerVisibility(waiting, isVisible = false)

            setUpScoreboard()
            scoreboard.root.hideAlpha(10)

            tvCluesDone.text = "$round"
            llCluesDone.handleAlpha(0.5f, 350)

            tvClue.isVisible = false
            tvClue.hideAlpha(10) { tvClue.text = null }

            clClue.isVisible = true
            clClue.showAlpha(350)
            tvTopText.text = getString(R.string.ranges_write_a_clue)
            etClue.text = null
            etClue.showAlpha(350)

            buttonAnotherRange.text = getString(R.string.ranges_another_range)
            buttonHide.button.text = getString(R.string.online_btn_save_answer)
            buttonCheck.button.text = getString(R.string.btn_check)
            buttonNextRound.button.text = getString(R.string.btn_next_round)

            controllerInfo.root.hideAlpha(1)
            controller.isEnabled = false

            ranges.apply {
                tvRangeLeft.hideAlpha(1)
                tvRangeRight.hideAlpha(1)
            }

            buttonAnotherRange.hideAlpha(1)
            buttonHide.root.hideAlpha(1)
            buttonCheck.root.animateY(500f, 1)
            buttonNextRound.root.animateY(500f, 1)

            arrowVisibility(isVisible = false)
            curtainVisibility(isVisible = true)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setListeners() {
        binding.apply {
            toolbar.apply {
                btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
                btnInstructions.setOnClickListener { openInstructions() }
            }

            buttonAnotherRange.setOnClickListener {
                buttonAnotherRange.isEnabled = false
                pleaseAnotherRange()
            }

            buttonHide.button.setOnClickListener {
                if (etClue.text.isNotEmpty()) {
                    buttonHide.button.isEnabled = false
                    saveData()
                }
            }

            buttonCheck.button.setOnClickListener {
                buttonCheck.button.isEnabled = false
                showGuessingResult()
            }

            buttonNextRound.root.setOnClickListener {
                buttonNextRound.root.isEnabled = false
                buttonNextRound.root.animateY(500f, 500)
                getReadyForGuessingPhase()
            }

            val parent = target.parent as View
            controller.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        target.tag = event.rawX - target.x
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val offsetX = target.tag as Float
                        var newX = event.rawX - offsetX

                        if (newX < 0f) newX = 0f
                        if (newX + target.width > parent.width) {
                            newX = (parent.width - target.width).toFloat()
                        }
                        target.x = newX

                        arrow.x = target.x + (target.width - arrow.width) / 2f
                    }
                }
                true
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is ORangesEvent.GetRanges -> {
                    rangesList = event.ranges.shuffled()
                    startCluePhase()
                }

                is ORangesEvent.OtherPlayerData -> {
                    waitingPartnerVisibility(binding.waiting, isVisible = false)
                    otherPlayerData = event.data
                    round = 1

                    binding.apply {
                        rlPartner.showAlpha(500) {
                            countDown(2000) {
                                rlPartner.hideAlpha(500) { startGuessingPhase() }
                            }
                        }
                    }
                }

                is ORangesEvent.OtherPlayerPoints -> {
                    waitingPartnerVisibility(binding.waiting, isVisible = false)
                    endGame(event.otherPlayerPoints)
                }

                is ORangesEvent.GameRestarted -> restartGame()
                is ORangesEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun startCluePhase() {
        binding.apply {
            loading.root.isVisible = false
            if (round <= 3) {
                tvCluesDone.text = "$round"
                if (round == 2) buttonHide.button.text = getString(R.string.online_btn_save_answer)
                buttonHide.button.isEnabled = true

                if (position > rangesList.size - 1) position = 0
                val actualRange = rangesList[position]
                ranges.tvRangeLeft.text = actualRange.leftRange
                ranges.tvRangeRight.text = actualRange.rightRange
                setRandomBullsEyePosition()
                countDown(750) {
                    clClue.showAlpha(350)

                    buttonAnotherRange.isEnabled = true
                    buttonAnotherRange.visibility = View.VISIBLE
                    buttonAnotherRange.showAlpha(350)

                    buttonHide.root.showAlpha(350)
                    curtainVisibility(isVisible = false)
                    ranges.apply {
                        tvRangeLeft.showAlpha(350)
                        tvRangeRight.showAlpha(350)
                    }
                }
            } else {
                waitingPartnerVisibility(waiting, isVisible = true)

                ranges.root.hideAlpha(500)
                clSlider.hideAlpha(500) {
                    clClue.isVisible = false
                    tvClue.isVisible = true
                }

                checkNotNulls(roomId, isCreator) { id, creator ->
                    val onlineData = OnlineData(
                        roomId = id, isCreator = creator, data = data
                    )

                    llCluesDone.hideAlpha(350)
                    viewModel.sendMyDataToRoom(onlineData)
                }
            }
        }
    }

    private fun saveData() {
        binding.apply {
            data.add(
                OnlineRoundData(
                    round = round,
                    bullseyePosition = bullseyePosition,
                    hint = etClue.text.toString(),
                    leftRange = rangesList[position].leftRange,
                    rightRange = rangesList[position].rightRange
                )
            )

            buttonAnotherRange.isEnabled = false
            buttonAnotherRange.hideAlpha(350) {
                buttonAnotherRange.visibility = View.INVISIBLE
            }
            buttonHide.root.hideAlpha( 350)

            curtainVisibility(isVisible = true) { setRandomBullsEyePosition() }
            ranges.apply {
                tvRangeLeft.hideAlpha(350)
                tvRangeRight.hideAlpha(350)
            }
            clClue.hideAlpha(350) { etClue.text = null }

            round++
            position++
            countDown(750) { startCluePhase() }
        }
    }

    private fun pleaseAnotherRange() {
        binding.apply {
            buttonAnotherRange.hideAlpha(350)

            curtainVisibility(isVisible = true) {
                position++
                if (position > rangesList.size - 1) position = 0
                val actualRange = rangesList[position]
                ranges.apply {
                    tvRangeLeft.hideAlpha(350) { tvRangeLeft.text = actualRange.leftRange }
                    tvRangeRight.hideAlpha(350) { tvRangeRight.text = actualRange.rightRange }
                }
                setRandomBullsEyePosition()
            }
            countDown(1500) {
                curtainVisibility(isVisible = false)
                ranges.apply {
                    tvRangeLeft.showAlpha(1000)
                    tvRangeRight.showAlpha(1000)
                }
            }
        }
    }

    private fun startGuessingPhase() {
        binding.apply {
            if (round <= 3) {
                val btnText = if (round == 3) getString(R.string.ranges_see_points)
                else getString(R.string.btn_next_round)
                buttonNextRound.button.text = btnText

                buttonCheck.button.isEnabled = true

                val actualRange = otherPlayerData[otherPlayerPosition]
                tvClue.text = actualRange.hint
                ranges.tvRangeLeft.text = actualRange.leftRange
                ranges.tvRangeRight.text = actualRange.rightRange
                bullseyeVisibility(isVisible = false)
                setBullsEyeWithPosition(position = actualRange.bullseyePosition)
                countDown(750) {
                    clSlider.showAlpha(350)
                    ranges.root.showAlpha(350)
                    scoreboard.root.showAlpha(350)
                    tvClue.showAlpha(350)
                    buttonCheck.root.animateY(0f, 350)
                    controllerInfo.root.handleAlpha(0.35f, 350)
                    controller.isEnabled = true
                    curtainVisibility(isVisible = false)
                    arrowVisibility(isVisible = true)
                    ranges.apply {
                        tvRangeLeft.showAlpha(350)
                        tvRangeRight.showAlpha(350)
                    }
                }
            } else {
                waitingPartnerVisibility(waiting, isVisible = true)
                checkNotNulls(roomId, isCreator) { id, creator ->
                    viewModel.sendMyPoints(id, creator, pointsObtained)
                }
            }
        }
    }

    private fun showGuessingResult() {
        binding.apply {
            controller.isEnabled = false
            controllerInfo.root.hideAlpha(350)
            buttonCheck.root.animateY(500f, 500)
            bullseyeVisibility(isVisible = true)

            checkPoints()

            round++
            otherPlayerPosition++
            countDown(1000) {
                buttonNextRound.button.isEnabled = true
                buttonNextRound.root.animateY(0f, 500)
            }
        }
    }

    private fun getReadyForGuessingPhase() {
        binding.apply {
            tvClue.hideAlpha(350)
            buttonCheck.root.animateY(500f, 350)
            controller.isEnabled = false
            curtainVisibility(isVisible = true)
            arrowVisibility(isVisible = false)
            ranges.apply {
                tvRangeLeft.hideAlpha(350)
                tvRangeRight.hideAlpha(350)
            }

            countDown(750) {
                arrow.translationX = 0f
                target.translationX = 0f
                startGuessingPhase()
            }
        }
    }

    private fun checkPoints() {
        binding.apply {
            val points = if (areViewsColliding(target, bullsEye.centerBullseye)) 5
            else if (areViewsColliding(target, bullsEye.twoLeftBullseye)) 2
            else if (areViewsColliding(target, bullsEye.twoRightBullseye)) 2
            else if (areViewsColliding(target, bullsEye.oneLeftBullseye)) 1
            else if (areViewsColliding(target, bullsEye.oneRightBullseye)) 1
            else 0

            if (points != 0) binding.konfetti.start(getKonfettiParty())
            pointsObtained += points

            scoreboardRangesAdapter?.updateScore(
                newScoreRange = ScoreRange(
                    discovered = true, points = points
                ), position = round - 1
            )
        }
    }

    private fun areViewsColliding(view1: View, view2: View): Boolean {
        val rect1 = Rect()
        val rect2 = Rect()

        val isVisible1 = view1.getGlobalVisibleRect(rect1)
        val isVisible2 = view2.getGlobalVisibleRect(rect2)

        return isVisible1 && isVisible2 && Rect.intersects(rect1, rect2)
    }

    private fun setRandomBullsEyePosition() {
        binding.apply {
            val parent = rlSlider
            val child = bullsEye.root

            parent.post {
                val parentWidth = parent.width
                val bullseyeWidth = child.width

                val totalWeight = 9.5f
                val p1Weight = 1.5f
                val p2Weight = 2f

                val w1 = bullseyeWidth * (p1Weight / totalWeight)
                val w2 = bullseyeWidth * (p2Weight / totalWeight)

                val lateralSectionsWidth = w1 + w2

                val minX = -lateralSectionsWidth
                val maxX = parentWidth - bullseyeWidth + lateralSectionsWidth

                val finalMin = minOf(minX, maxX)
                val finalMax = maxOf(minX, maxX)

                val randomX = kotlin.random.Random.nextFloat() * (finalMax - finalMin) + finalMin
                bullseyePosition = randomX / (parentWidth - bullseyeWidth)

                child.x = randomX
            }
        }
    }

    private fun setBullsEyeWithPosition(position: Float) {
        binding.apply {
            val parent = rlSlider
            val child = bullsEye.root

            parent.post {
                val parentWidth = parent.width
                val childWidth = child.width

                child.x = position * (parentWidth - childWidth)
            }
        }
    }

    private fun arrowVisibility(isVisible: Boolean) {
        binding.apply {
            val view = if (isVisible) View.VISIBLE else View.INVISIBLE
            target.visibility = view
            arrow.visibility = view
        }
    }

    private fun curtainVisibility(isVisible: Boolean, onEnd: () -> Unit = {}) {
        binding.apply {
            if (isVisible) {
                curtainLeft.animateX(0f, 500) { onEnd() }
                curtainRight.animateX(0f, 500) { onEnd() }
            } else {
                curtainLeft.animateX(-1000f, 1000) { onEnd() }
                curtainRight.animateX(1000f, 1000) { onEnd() }
            }
        }
    }

    private fun bullseyeVisibility(isVisible: Boolean) {
        val bullsEye = if (_binding != null) binding.bullsEye.root else null
        if (isVisible) bullsEye?.showAlpha(500)
        else bullsEye?.hideAlpha(1)
    }

    private fun endGame(otherPlayerPoints: Int) {
        activity?.showFragmentDialog(
            EndGameORangesDialog(myPoints = pointsObtained,
                otherPlayerPoints = otherPlayerPoints,
                exit = {
                    if (activity is BedRockActivity) {
                        (activity as BedRockActivity).skipExitDialog = true
                    }
                    activity?.onBackPressedDispatcher?.onBackPressed()
                },
                replay = {
                    checkNotNulls(roomId, isCreator) { id, creator ->
                        if (creator) {
                            binding.loading.root.isVisible = true
                            viewModel.restartGame(id)
                        } else {
                            waitingPartnerVisibility(binding.waiting, isVisible = true)
                            viewModel.waitCreatorToRestart(id)
                        }
                    }
                })
        )
    }

    private fun restartGame() {
        position++
        round = 1
        otherPlayerPosition = 0
        pointsObtained = 0

        data.clear()
        otherPlayerData = emptyList()

        waitingPartnerVisibility(binding.waiting, isVisible = false)

        setUI()
        startCluePhase()
    }

    private fun openInstructions() = (activity as BedRockActivity).openBedRockActivity(
        navGraph = R.navigation.nav_graph_instructions, strArgs = RANGES_TYPE
    )

    private fun error() {
        if (activity is BedRockActivity) {
            (activity as BedRockActivity).skipExitDialog = true
        }
        activity?.showErrorDialog()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}