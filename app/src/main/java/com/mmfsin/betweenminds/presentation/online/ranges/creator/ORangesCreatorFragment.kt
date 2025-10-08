package com.mmfsin.betweenminds.presentation.online.ranges.creator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentRangesOnlineCreatorBinding
import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.models.OnlineRoundData
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.ScoreRange
import com.mmfsin.betweenminds.presentation.online.common.dialog.WaitingOtherPlayerDialog
import com.mmfsin.betweenminds.presentation.online.ranges.dialogs.EndGameORangesDialog
import com.mmfsin.betweenminds.presentation.ranges.adapter.ScoreboardRangesAdapter
import com.mmfsin.betweenminds.utils.BEDROCK_BOOLEAN_ARGS
import com.mmfsin.betweenminds.utils.BEDROCK_STR_ARGS
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.checkNotNulls
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyOScoreRangesList
import com.mmfsin.betweenminds.utils.getKonfettiParty
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ORangesCreatorFragment :
    BaseFragment<FragmentRangesOnlineCreatorBinding, ORangesCreatorViewModel>() {

    override val viewModel: ORangesCreatorViewModel by viewModels()

    private lateinit var mContext: Context

    var roomId: String? = null
    var isCreator: Boolean? = null

    private var rangesList: List<Range> = emptyList()
    private var position = 0
    private var bullseyePosition = 0
    private var round = 1

    private val data = mutableListOf<OnlineRoundData>()

    private var otherPlayerData: List<OnlineRoundData> = emptyList()
    private var otherPlayerPosition = 0
    private var pointsObtained = 0

    private var waitingDialog: WaitingOtherPlayerDialog? = null

    private var scoreboardRangesAdapter: ScoreboardRangesAdapter? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRangesOnlineCreatorBinding.inflate(inflater, container, false)

    override fun getBundleArgs() {
        activity?.intent?.apply {
            roomId = getStringExtra(BEDROCK_STR_ARGS)
            isCreator = getBooleanExtra(BEDROCK_BOOLEAN_ARGS, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpScoreboard()
        checkNotNulls(roomId, isCreator) { _, _ -> viewModel.getRanges() } ?: run { error() }
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
            scoreboard.root.visibility = View.INVISIBLE
            scoreboard.root.hideAlpha(10)

            buttonHide.button.text = getString(R.string.online_btn_save_answer)
            buttonCheck.button.text = getString(R.string.btn_check)
            buttonNextRound.button.text = getString(R.string.btn_next_round)

            controllerInfo.root.hideAlpha(1)
            controller.isEnabled = false

            ranges.apply {
                tvRangeLeft.hideAlpha(1)
                tvRangeRight.hideAlpha(1)
            }

            buttonHide.root.animateY(500f, 1)
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
                is ORangesCreatorEvent.GetRanges -> {
                    rangesList = event.ranges.shuffled()
                    startCluePhase()
                }

                is ORangesCreatorEvent.OtherPlayerData -> {
                    waitingDialog?.dismiss()
                    otherPlayerData = event.data

                    round = 1
                    startGuessingPhase()
                }

                is ORangesCreatorEvent.OtherPlayerPoints -> {
                    waitingDialog?.dismiss()
                    endGame(event.otherPlayerPoints)
                }

                is ORangesCreatorEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun startCluePhase() {
        binding.apply {
            if (round <= 3) {
                if (round == 2) buttonHide.button.text = getString(R.string.online_btn_save_answer)
                buttonHide.button.isEnabled = true

                val actualRange = rangesList[position]
                ranges.tvRangeLeft.text = actualRange.leftRange
                ranges.tvRangeRight.text = actualRange.rightRange
                setBullsEye()
                countDown(1000) {
                    clClue.showAlpha(350)
                    buttonHide.root.animateY(0f, 350)
                    curtainVisibility(isVisible = false)
                    ranges.apply {
                        tvRangeLeft.showAlpha(350)
                        tvRangeRight.showAlpha(350)
                    }
                }
            } else {
                waitingDialog = WaitingOtherPlayerDialog()
                waitingDialog?.let { d -> activity?.showFragmentDialog(d) }

                checkNotNulls(roomId, isCreator) { id, creator ->
                    val onlineData = OnlineData(
                        roomId = id, isCreator = creator, data = data
                    )
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

            buttonHide.root.animateY(500f, 350)
            curtainVisibility(isVisible = true) { setBullsEye() }
            ranges.apply {
                tvRangeLeft.hideAlpha(350)
                tvRangeRight.hideAlpha(350)
            }
            clClue.hideAlpha(350) { etClue.text = null }

            round++
            position++
            countDown(1000) { startCluePhase() }
        }
    }

    private fun startGuessingPhase() {
        binding.apply {
            if (round <= 3) {
                if (round == 3) buttonNextRound.button.text = getString(R.string.ranges_see_points)
                buttonCheck.button.isEnabled = true

                scoreboard.root.visibility = View.VISIBLE
                etClue.visibility = View.INVISIBLE
                tvClue.visibility = View.VISIBLE
                tvTopText.text = getString(R.string.ranges_clue_title)

                val actualRange = otherPlayerData[otherPlayerPosition]
                tvClue.text = actualRange.hint
                ranges.tvRangeLeft.text = actualRange.leftRange
                ranges.tvRangeRight.text = actualRange.rightRange
                setBullsEye(position = actualRange.bullseyePosition)
                bullseyeVisibility(isVisible = false)
                countDown(1000) {
                    scoreboard.root.showAlpha(350)
                    clClue.showAlpha(350)
                    buttonCheck.root.animateY(0f, 350)
                    controllerInfo.root.showAlpha(350)
                    controller.isEnabled = true
                    curtainVisibility(isVisible = false)
                    arrowVisibility(isVisible = true)
                    ranges.apply {
                        tvRangeLeft.showAlpha(350)
                        tvRangeRight.showAlpha(350)
                    }
                }
            } else {
                waitingDialog = WaitingOtherPlayerDialog()
                waitingDialog?.let { d -> activity?.showFragmentDialog(d) }

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
            clClue.hideAlpha(350)
            buttonCheck.root.animateY(500f, 350)
            controller.isEnabled = false
            curtainVisibility(isVisible = true)
            arrowVisibility(isVisible = false)
            ranges.apply {
                tvRangeLeft.hideAlpha(350)
                tvRangeRight.hideAlpha(350)
            }

            countDown(1000) {
                arrow.translationX = 0f
                target.translationX = 0f
                startGuessingPhase()
            }
        }
    }

    private fun checkPoints() {
        binding.apply {
            val points = if (areViewsColliding(target, bullsEye.centerBullseye)) 5
            else if (areViewsColliding(target, bullsEye.rightBullseye)) 2
            else if (areViewsColliding(target, bullsEye.leftBullseye)) 2
            else 0

            if (points != 0) binding.konfetti.start(getKonfettiParty())
            pointsObtained += points

            scoreboardRangesAdapter?.updateScore(
                newScoreRange = ScoreRange(
                    discovered = true,
                    points = points
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

    private fun setBullsEye(position: Int? = null) {
        binding.apply {
            val parent = rlSlider
            val child = bullsEye.root
            parent.post {

                if (position == null) {
                    val parentWidth = parent.width
                    val bullseyeWidth = child.width

                    val centerOffset = (0.2f * bullseyeWidth) + (0.35f * bullseyeWidth) / 2f

                    val minX = -centerOffset
                    val maxX = parentWidth - (bullseyeWidth - centerOffset)

                    val randomX = (minX.toInt()..maxX.toInt()).random()
                    bullseyePosition = randomX
                    child.x = randomX.toFloat()

                } else child.x = position.toFloat()
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
                curtainLeft.animateX(0f, 1000) { onEnd() }
                curtainRight.animateX(0f, 1000) { onEnd() }
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
            EndGameORangesDialog(
                myPoints = pointsObtained,
                otherPlayerPoints = otherPlayerPoints,
                exit = { activity?.onBackPressedDispatcher?.onBackPressed() },
                replay = {}
            )
        )
    }

    private fun openInstructions() =
        (activity as BedRockActivity).openBedRockActivity(R.navigation.nav_graph_instr_ranges)

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}