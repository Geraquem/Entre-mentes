package com.mmfsin.betweenminds.presentation.question

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
import com.mmfsin.betweenminds.databinding.FragmentQuestionAuxiliarBinding
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.presentation.common.dialogs.EndGameDialog
import com.mmfsin.betweenminds.presentation.ranges.adapter.ScoreboardRangesAdapter
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyScoreRangesList
import com.mmfsin.betweenminds.utils.handleAlpha
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionAuxiliarFragment :
    BaseFragment<FragmentQuestionAuxiliarBinding, QuestionViewModel>() {

    override val viewModel: QuestionViewModel by viewModels()

    private lateinit var mContext: Context

    private var questionList: List<Question> = emptyList()
    private var position = 0
    private var round = 1

    private var scoreboardRangesAdapter: ScoreboardRangesAdapter? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentQuestionAuxiliarBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpScoreboard()
        viewModel.getQuestions()
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

            llRound.showAlpha(1)
            roundNumber.text = "$round"

            tvQuestion.hideAlpha(1)

            controllerInfo.root.hideAlpha(1)
            controller.isEnabled = false

//            ranges.apply {
//                tvRangeLeft.hideAlpha(1)
//                tvRangeRight.hideAlpha(1)
//            }

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

            val parent = firstOpinion.parent as View
            controller.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        firstOpinion.tag = event.rawX - firstOpinion.x
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val offsetX = firstOpinion.tag as Float
                        var newX = event.rawX - offsetX

                        if (newX < 0f) newX = 0f
                        if (newX + firstOpinion.width > parent.width) {
                            newX = (parent.width - firstOpinion.width).toFloat()
                        }
                        firstOpinion.x = newX
                        arrow.x = firstOpinion.x + (firstOpinion.width - arrow.width) / 2f
                    }
                }
                true
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is QuestionEvent.Questions -> {
                    binding.loading.root.isVisible = false
                    questionList = event.questions
                    showInitialDialog()
                }

                is QuestionEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun showInitialDialog() {
        binding.llRound.hideAlpha(1)
        setFirstRanges()
//        activity?.showFragmentDialog(
//            RangesStartDialog(
//                start = { showRound { setFirstRanges() } },
//                instructions = { openInstructions() }
//            )
//        )
    }

    private fun showRound(onEnd: () -> Unit) {
        try {
            binding.apply {
                llRound.showAlpha(500) {
                    countDown(500) {
                        llRound.hideAlpha(500) { onEnd() }
                    }
                }
            }
        } catch (e: Exception) {
            println("Error with binding not attached")
        }
    }

    private fun setFirstRanges() {
        binding.apply {
            try {
                val actualQuestion = questionList[position]
                tvQuestion.text = actualQuestion.text

                countDown(500) { firstPhase() }
            } catch (e: Exception) {
                error()
            }
        }
    }

    private fun initialStates() {
        binding.apply {
            tvQuestion.hideAlpha(350) {
                tvQuestion.text = getString(R.string.ranges_write_a_clue)
            }
            tvQuestion.hideAlpha(350)
            arrowVisibility(isVisible = false)

            arrow.translationX = 0f
            firstOpinion.translationX = 0f

            buttonHide.root.isEnabled = true
            buttonCheck.root.isEnabled = true
            buttonNextRound.root.isEnabled = true

            if (round > 3) buttonNextRound.button.text = getString(R.string.ranges_see_points)
        }
    }

    private fun firstPhase() {
        binding.apply {
            scoreboardRangesAdapter?.roundColor(round - 1)
            countDown(500) {
                curtainVisibility(isVisible = false)
                tvQuestion.showAlpha(1000)
                arrowVisibility(isVisible = true)

                controller.isEnabled = true
                controllerInfoVisibility(true)

                buttonHide.root.animateY(0f, 1000)
            }
        }
    }

    private fun secondPhase() {
        binding.apply {
            tvQuestion.hideAlpha(500)

            buttonHide.root.animateY(500f, 500)
            curtainVisibility(isVisible = true) {
//                bullseyeVisibility(isVisible = false)
            }

            countDown(1500) {
                tvQuestion.showAlpha(1000)
                controllerInfoVisibility(true)

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
            controllerInfoVisibility(false)
            buttonCheck.root.animateY(500f, 500)
//            bullseyeVisibility(isVisible = true)

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

            if (position > questionList.size - 1) position = 0
            val actualRange = questionList[position]
//            ranges.apply {
//                tvRangeLeft.hideAlpha(350) { tvRangeLeft.text = actualRange.leftRange }
//                tvRangeRight.hideAlpha(350) { tvRangeRight.text = actualRange.rightRange }
//            }

            showRound { firstPhase() }
        }
    }

    private fun checkPoints() {
        binding.apply {
//            val points = if (areViewsColliding(target, bullsEye.centerBullseye)) 5
//            else if (areViewsColliding(target, bullsEye.rightBullseye)) 2
//            else if (areViewsColliding(target, bullsEye.leftBullseye)) 2
//            else 0
//
//            if (points != 0) binding.konfetti.start(getKonfettiParty())
//
//            scoreboardRangesAdapter?.updateScore(
//                newScoreRange = ScoreRange(
//                    discovered = true, points = points
//                ), position = round - 1
//            )
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
            firstOpinion.visibility = view
            arrow.visibility = view
        }
    }

    private fun curtainVisibility(isVisible: Boolean, onEnd: () -> Unit = {}) {
        try {
            binding.apply {
                if (isVisible) {
                    curtainLeft.animateX(0f, 1000) { onEnd() }
                    curtainRight.animateX(0f, 1000) { onEnd() }
                } else {
                    curtainLeft.animateX(-1000f, 1000) { onEnd() }
                    curtainRight.animateX(1000f, 1000) { onEnd() }
                }
            }
        } catch (e: Exception) {
            println("Error with binding not attached")
        }
    }

    private fun controllerInfoVisibility(isVisible: Boolean) {
        val cInfo = binding.controllerInfo.root
        if (isVisible) cInfo.handleAlpha(0.35f, 350)
        else cInfo.hideAlpha(350)
    }

    private fun endGame() {
        endGameStates()
        val points = scoreboardRangesAdapter?.getTotalPoints()
        points?.let {
            activity?.showFragmentDialog(
                EndGameDialog(points = points,
                    restartGame = { restartGame() },
                    saveScore = {  /*showSaveScoreDialog(points)*/ },
                    exit = { activity?.onBackPressedDispatcher?.onBackPressed() })
            )
        } ?: run { error() }
    }

    private fun endGameStates() {
        binding.apply {
            curtainVisibility(isVisible = true)
//            ranges.apply {
//                tvRangeLeft.hideAlpha(200)
//                tvRangeRight.hideAlpha(200)
//            }
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

    private fun openInstructions() =
        (activity as BedRockActivity).openBedRockActivity(R.navigation.nav_graph_instr_ranges)

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}