package com.mmfsin.betweenminds.presentation.ranges.offline

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
import com.mmfsin.betweenminds.databinding.FragmentRangesBinding
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.ScoreRange
import com.mmfsin.betweenminds.presentation.ranges.adapter.ScoreboardRangesAdapter
import com.mmfsin.betweenminds.presentation.ranges.dialogs.EndRangesDialog
import com.mmfsin.betweenminds.presentation.ranges.dialogs.RangesStartDialog
import com.mmfsin.betweenminds.utils.RANGES_TYPE
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyScoreRangesList
import com.mmfsin.betweenminds.utils.getKonfettiParty
import com.mmfsin.betweenminds.utils.handleAlpha
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RangesFragment : BaseFragment<FragmentRangesBinding, RangesViewModel>() {

    override val viewModel: RangesViewModel by viewModels()

    private lateinit var mContext: Context

    private var rangesList: List<Range> = emptyList()
    private var position = 0
    private var round = 1

    private var scoreboardRangesAdapter: ScoreboardRangesAdapter? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRangesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpScoreboard()
        viewModel.getRanges()
    }

    private fun setUpScoreboard() {
        binding.apply {
            scoreboard.rvScore.apply {
                layoutManager = GridLayoutManager(mContext, 4)
                scoreboardRangesAdapter = ScoreboardRangesAdapter(getEmptyScoreRangesList())
                adapter = scoreboardRangesAdapter
            }
        }
    }

    override fun setUI() {
        binding.apply {
            loading.root.isVisible = true

            buttonHide.button.text = getString(R.string.btn_hide)
            buttonCheck.button.text = getString(R.string.btn_check)
            buttonNextRound.button.text = getString(R.string.btn_next_round)

            roundNumber.text = "$round"

            tvTopText.hideAlpha(1)
            etClue.hideAlpha(1)
            tvClue.hideAlpha(1)

            bullsEye.root.translationX = 50f

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

            buttonHide.root.setOnClickListener {
                buttonHide.root.isEnabled = false
                secondPhase()
            }

            buttonCheck.root.setOnClickListener {
                buttonCheck.root.isEnabled = false
                thirdPhase()
            }

            buttonNextRound.root.setOnClickListener {
                buttonNextRound.root.isEnabled = false
                buttonNextRound.root.animateY(500f, 500)
                if (round > 3) endGame() else nextRange()
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
                is RangesEvent.Ranges -> {
                    binding.loading.root.isVisible = false
                    rangesList = event.ranges.shuffled()
                    showInitialDialog()
                }

                is RangesEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun showInitialDialog() {
        activity?.showFragmentDialog(
            RangesStartDialog(
                close = { activity?.onBackPressedDispatcher?.onBackPressed() },
                start = { showRound { setFirstRanges() } },
                instructions = { openInstructions() })
        )
    }

    private fun showRound(onEnd: () -> Unit) {
        binding.apply {
            llRound.showAlpha(500) {
                llRound.hideAlpha(500) { onEnd() }
            }
        }
    }

    private fun setFirstRanges() {
        binding.apply {
            val actualRange = rangesList[position]
            ranges.tvRangeLeft.text = actualRange.leftRange
            ranges.tvRangeRight.text = actualRange.rightRange
            firstPhase()
        }
    }

    private fun initialStates() {
        binding.apply {
            tvTopText.hideAlpha(350) {
                tvTopText.text = getString(R.string.ranges_write_a_clue)
            }
            tvClue.hideAlpha(350)
            arrowVisibility(isVisible = false)

            arrow.translationX = 0f
            target.translationX = 0f

            buttonHide.root.isEnabled = true
            buttonCheck.root.isEnabled = true
            buttonNextRound.root.isEnabled = true

            if (round > 3) buttonNextRound.button.text = getString(R.string.ranges_see_points)
        }
    }

    private fun firstPhase() {
        binding.apply {
            scoreboardRangesAdapter?.roundColor(round - 1)
            setBullsEye()
            countDown(500) {
                curtainVisibility(isVisible = false)
                tvTopText.showAlpha(1000)
                etClue.showAlpha(1000) { etClue.isEnabled = true }
                ranges.tvRangeLeft.showAlpha(1000)
                ranges.tvRangeRight.showAlpha(1000)

                buttonHide.root.animateY(0f, 1000)
            }
        }
    }

    private fun secondPhase() {
        binding.apply {
            val clue = etClue.text.toString()

            tvTopText.hideAlpha(500)
            etClue.isEnabled = false
            etClue.hideAlpha(500) { etClue.text = null }
            tvClue.text = clue

            buttonHide.root.animateY(500f, 500)
            curtainVisibility(isVisible = true) {
                bullseyeVisibility(isVisible = false)
            }

            countDown(1500) {
                if (clue.isBlank()) {
                    tvClue.text = getString(R.string.ranges_no_clue)
                } else {
                    tvClue.text = clue
                    tvTopText.text = getString(R.string.ranges_clue_title)
                    tvTopText.showAlpha(1000)
                }
                tvClue.showAlpha(1000)
                controllerInfo.root.handleAlpha(0.35f, 1000)

                controller.isEnabled = true
                curtainVisibility(isVisible = false)
                arrowVisibility(isVisible = true)
                buttonCheck.root.animateY(0f, 500)
            }
        }
    }

    private fun thirdPhase() {
        binding.apply {
            controller.isEnabled = false
            controllerInfo.root.hideAlpha(350)
            buttonCheck.root.animateY(500f, 500)
            bullseyeVisibility(isVisible = true)

            checkPoints()

            countDown(1000) {
                buttonNextRound.root.animateY(0f, 500)
            }
        }
    }

    private fun nextRange() {
        binding.apply {
            round++
            roundNumber.text = "$round"

            initialStates()
            position++
            curtainVisibility(isVisible = true)

            if (position > rangesList.size - 1) position = 0
            val actualRange = rangesList[position]
            ranges.apply {
                tvRangeLeft.hideAlpha(350) { tvRangeLeft.text = actualRange.leftRange }
                tvRangeRight.hideAlpha(350) { tvRangeRight.text = actualRange.rightRange }
            }

            showRound { firstPhase() }
        }
    }

    private fun setBullsEye() {
        binding.apply {
            val parent = rlSlider
            val child = bullsEye.root

            val parentWidth = parent.width
            val bullseyeWidth = child.width

            parent.post {
                val centerOffset = (0.2f * bullseyeWidth) + (0.35f * bullseyeWidth) / 2f

                val minX = -centerOffset
                val maxX = parentWidth - (bullseyeWidth - centerOffset)

                val randomX = (minX.toInt()..maxX.toInt()).random()
                child.x = randomX.toFloat()
//                child.x = minX
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

    private fun endGame() {
        endGameStates()
        val points = scoreboardRangesAdapter?.getTotalPoints()
        points?.let {
            activity?.showFragmentDialog(
                EndRangesDialog(
                    points = points,
                    restartGame = { restartGame() },
                    exit = { activity?.onBackPressedDispatcher?.onBackPressed() })
            )
        } ?: run { error() }
    }

    private fun endGameStates() {
        binding.apply {
            curtainVisibility(isVisible = true)
            ranges.apply {
                tvRangeLeft.hideAlpha(200)
                tvRangeRight.hideAlpha(200)
            }
            initialStates()
        }
    }

    private fun restartGame() {
        round = 1
        binding.roundNumber.text = "$round"
        position++
        scoreboardRangesAdapter?.resetScores()

        showRound { setFirstRanges() }
    }

    private fun openInstructions() = (activity as BedRockActivity).openBedRockActivity(
        navGraph = R.navigation.nav_graph_instructions,
        strArgs = RANGES_TYPE,
        booleanArgs = false
    )

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}