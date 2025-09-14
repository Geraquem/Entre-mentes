package com.mmfsin.betweenminds.presentation.range

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentRangeBinding
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.Score
import com.mmfsin.betweenminds.presentation.common.adapter.ScoreboardAdapter
import com.mmfsin.betweenminds.presentation.common.dialogs.EndGameDialog
import com.mmfsin.betweenminds.presentation.common.dialogs.save.SavePointsDialog
import com.mmfsin.betweenminds.utils.MODE_NUMBER
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyScoreList
import com.mmfsin.betweenminds.utils.getNumberColor
import com.mmfsin.betweenminds.utils.getPoints
import com.mmfsin.betweenminds.utils.handleAlpha
import com.mmfsin.betweenminds.utils.handleSliderTrackColor
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.moveSliderValue
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
class RangesFragment : BaseFragment<FragmentRangeBinding, RangesViewModel>() {

    override val viewModel: RangesViewModel by viewModels()

    private lateinit var mContext: Context

    private var ranges: List<Range> = emptyList()
    private var position = 0

    private var numberToGuess = 0
    private var resultNumber = 0
    private var round = 0

    private var scoreboardAdapter: ScoreboardAdapter? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRangeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpScoreboard()
        viewModel.getRanges()
    }

    private fun setUpScoreboard() {
        binding.apply {
            scoreboard.rvScore.apply {
                layoutManager = GridLayoutManager(mContext, 4)
                scoreboardAdapter = ScoreboardAdapter(getEmptyScoreList())
                adapter = scoreboardAdapter
            }
        }
    }

    override fun setUI() {
        binding.apply {
            (activity as BedRockActivity).setUpToolbar(
                instructionsNavGraph = R.navigation.nav_graph_question
            )

            loading.root.isVisible = true

            topSlider.isEnabled = false
            topSlider.thumbTintList = ColorStateList.valueOf(Color.WHITE)
            bottomSlider.thumbTintList = ColorStateList.valueOf(Color.WHITE)

            rlBtnHide.animateY(500f, 1)
            rlBtnCheck.animateY(500f, 1)
            rlBtnRematch.animateY(500f, 1)

            initialStates()
        }
    }

    private fun initialStates() {
        binding.apply {
            hideCurtain()

            topSlider.moveSliderValue(0)

            bottomSlider.isEnabled = false
            llBottomSlider.handleAlpha(0.4f, 350)
            bottomSlider.moveSliderValue(0)

            btnHide.isEnabled = true
            btnCheck.isEnabled = true
            rematch.btnRematch.isEnabled = true
        }
    }

    private fun startGame() {
        binding.apply {
            tvTopNumber.setTextColor(getColor(mContext, R.color.dark_grey))
            val finalNumber = (-100..100).random()
            numberToGuess = finalNumber
            val animator = ValueAnimator.ofInt(1, 100)
            animator.duration = 2000
            animator.addUpdateListener { _ ->
                val random = (0..100).random()
                tvTopNumber.text = "$random"
            }
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    tvTopNumber.text = "${finalNumber.absoluteValue}"
                    tvTopNumber.setTextColor(getColor(mContext, getNumberColor(finalNumber)))
                    topSlider.moveSliderValue(finalNumber)
                    mContext.handleSliderTrackColor(finalNumber, topSlider)

                    countDown(350) {
                        rlBtnHide.animateY(0f, 500)
                    }
                }
            })
            animator.start()
        }
    }

    override fun setListeners() {
        binding.apply {
            bottomSlider.addOnChangeListener { _, value, _ -> setSliderValue(value.toInt()) }

            btnHide.setOnClickListener {
                btnHide.isEnabled = false
                showCurtain()
                rlBtnHide.animateY(500f, 500)

                countDown(350) {
                    llBottomSlider.showAlpha(500) { bottomSlider.isEnabled = true }
                    rlBtnCheck.animateY(0f, 500)
                }
            }

            btnCheck.setOnClickListener {
                btnCheck.isEnabled = false
                bottomSlider.isEnabled = false
                hideCurtain()
                setScoreRound()
                rlBtnCheck.animateY(500f, 500)

                countDown(500) {
                    /** Cuatro rondas 0,1,2,3 */
                    if (round > 2) countDown(1000) { endGame() }
                    else rlBtnRematch.animateY(0f, 500)
                }
            }

            rematch.btnRematch.setOnClickListener {
                round++
                rematch.btnRematch.isEnabled = false
                rlBtnRematch.animateY(500f, 500)
                countDown(200) {
                    initialStates()
                    nextRange()
                }
            }
        }
    }

    private fun nextRange() {
        binding.apply {
            position++
            countDown(500) {
                if (position > ranges.size - 1) position = 0
                val actualRange = ranges[position]
                tvRangeLeft.text = actualRange.leftRange
                tvRangeRight.text = actualRange.rightRange
                startGame()
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is RangesEvent.Ranges -> setRanges(event.ranges)
                is RangesEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setRanges(ranges: List<Range>) {
        binding.apply {
            try {
                this@RangesFragment.ranges = ranges
                val actualRange = ranges[position]
                tvRangeLeft.text = actualRange.leftRange
                tvRangeRight.text = actualRange.rightRange

                countDown(1000) {
                    loading.root.isVisible = false
                    startGame()
                }
            } catch (e: Exception) {
                error()
            }
        }
    }

    private fun showCurtain() {
        binding.apply { curtain.showAlpha(350) }
    }

    private fun hideCurtain() {
        binding.apply { curtain.hideAlpha(500) }
    }

    private fun setSliderValue(value: Int) {
        binding.apply {
            resultNumber = value
            tvBottomNumber.text = "${value.absoluteValue}"
            tvBottomNumber.setTextColor(getColor(mContext, getNumberColor(value)))
            mContext.handleSliderTrackColor(value, bottomSlider)
        }
    }

    private fun setScoreRound() {
        scoreboardAdapter?.updateScore(
            newScore = Score(
                discovered = true,
                topNumber = numberToGuess,
                resultNumber = resultNumber,
                points = getPoints(numberToGuess, resultNumber)
            ), position = round
        )
    }

    private fun endGame() {
        val points = scoreboardAdapter?.getTotalPoints()
        points?.let {
            activity?.showFragmentDialog(EndGameDialog(points = points,
                restartGame = { restartGame() },
                saveScore = { showSaveScoreDialog(points) },
                exit = { activity?.onBackPressedDispatcher?.onBackPressed() }))
        } ?: run { error() }
    }

    private fun showSaveScoreDialog(points: Int) {
        activity?.showFragmentDialog(SavePointsDialog(mode = MODE_NUMBER,
            points = points,
            restartGame = { restartGame() },
            exit = { activity?.onBackPressedDispatcher?.onBackPressed() }))
    }

    private fun restartGame() {
        round = 0
        scoreboardAdapter?.resetScores()
        initialStates()
        startGame()
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

