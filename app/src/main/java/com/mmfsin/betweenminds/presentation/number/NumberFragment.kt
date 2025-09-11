package com.mmfsin.betweenminds.presentation.number

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentNumberBinding
import com.mmfsin.betweenminds.domain.models.Score
import com.mmfsin.betweenminds.presentation.number.adapter.ScoreboardAdapter
import com.mmfsin.betweenminds.presentation.number.dialogs.EndGameDialog
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyScoreList
import com.mmfsin.betweenminds.utils.getNumberColor
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.moveSliderValue
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showFragmentDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
class NumberFragment : BaseFragmentNoVM<FragmentNumberBinding>() {

    private lateinit var mContext: Context

    private var numberToGuess = 0
    private var resultNumber = 0
    private var round = 0

    private var scoreboardAdapter: ScoreboardAdapter? = null

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentNumberBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpScoreboard()
    }

    private fun setUpScoreboard() {
        binding.apply {
            scoreboard.rvScore.apply {
                layoutManager = GridLayoutManager(requireContext(), 4)
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

            lottieCurtain.isVisible = false

            topSlider.isEnabled = false
            llSlider.alpha = 0f

            llBtnHide.animateY(500f, 1)
            llBtnCheck.animateY(500f, 1)
            rematch.root.animateX(500f, 1)

            startGame()
        }
    }

    private fun startGame() {
        binding.apply {
            hideCurtain()
            slotMachine()

            topSlider.moveSliderValue(0f)
            llSlider.hideAlpha(350) {
                slider.value = 0f
                setSliderValue(0f)
            }

            btnHide.isEnabled = true
            btnCheck.isEnabled = true
            rematch.btnRematch.isEnabled = true
        }
    }

    private fun slotMachine() {
        binding.apply {
            tvNumberToGuess.setTextColor(getColor(mContext, R.color.dark_grey))
            val finalNumber = (-100..100).random()
            numberToGuess = finalNumber
            val animator = ValueAnimator.ofInt(1, 100)
            animator.duration = 2000
            animator.addUpdateListener { _ ->
                val random = (0..100).random()
                tvNumberToGuess.text = "$random"
            }
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    tvNumberToGuess.text = "${finalNumber.absoluteValue}"
                    tvNumberToGuess.setTextColor(getColor(mContext, getNumberColor(finalNumber)))
                    topSlider.moveSliderValue(finalNumber.toFloat())

                    countDown(350) {
                        llBtnHide.animateY(0f, 500)
                    }
                }
            })
            animator.start()
        }
    }

    override fun setListeners() {
        binding.apply {
            slider.addOnChangeListener { _, value, _ -> setSliderValue(value) }

            btnHide.setOnClickListener {
                btnHide.isEnabled = false
                showCurtain()
                llBtnHide.animateY(500f, 500)

                countDown(350) {
                    llSlider.showAlpha(500) { slider.isEnabled = true }
                    llBtnCheck.animateY(0f, 500)
                }
            }

            btnCheck.setOnClickListener {
                btnCheck.isEnabled = false
                slider.isEnabled = false
                hideCurtain()
                addPoints()
                llBtnCheck.animateY(500f, 500)
                countDown(500) {
                    /** Cuatro rondas 0,1,2,3 */
                    if (round > 2) countDown(1000) { endGame() }
                    else rematch.root.animateX(0f, 500)
                }
            }

            rematch.btnRematch.setOnClickListener {
                round++
                rematch.btnRematch.isEnabled = false
                rematch.root.animateX(500f, 500)
                countDown(200) { startGame() }
            }
        }
    }

    private fun showCurtain() {
        binding.apply {
            lottieCurtain.isVisible = true
            lottieCurtain.speed = 4f
            lottieCurtain.playAnimation()
        }
    }

    private fun hideCurtain() {
        binding.apply {
            lottieCurtain.animateY(-1000f, 500) {
                lottieCurtain.isVisible = false
                lottieCurtain.animateY(0f, 1)
            }
        }
    }

    private fun setSliderValue(value: Float) {
        binding.apply {
            resultNumber = value.toInt()
            tvSliderNumber.text = "${value.toInt().absoluteValue}"
            tvSliderNumber.setTextColor(getColor(mContext, getNumberColor(value.toInt())))

            val leftScale = 3f - ((value + 100f) / 200f) * 2f
            val rightScale = 1f + ((value + 100f) / 200f) * 2f

            ivRight.scaleX = leftScale
            ivRight.scaleY = leftScale

            ivLeft.scaleX = rightScale
            ivLeft.scaleY = rightScale

            if (value > 0) {
                ivLeft.setImageResource(R.drawable.ic_human_up)
                ivRight.setImageResource(R.drawable.ic_human_down)
            } else if (value == 0f) {
                ivLeft.setImageResource(R.drawable.ic_human_down)
                ivRight.setImageResource(R.drawable.ic_human_down)
            } else {
                ivLeft.setImageResource(R.drawable.ic_human_down)
                ivRight.setImageResource(R.drawable.ic_human_up)
            }
        }
    }

    private fun addPoints() {
        scoreboardAdapter?.updateScore(
            newScore = Score(
                discovered = true,
                topNumber = numberToGuess,
                resultNumber = resultNumber,
                points = 11
            ),
            position = round
        )
    }

    private fun endGame() {
        activity?.showFragmentDialog(
            EndGameDialog(
                points = 11,
                restartGame = {
                    round = 0
                    scoreboardAdapter?.resetScores()
                    startGame()
                },
                exit = { activity?.onBackPressedDispatcher?.onBackPressed() }
            )
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

