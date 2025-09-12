package com.mmfsin.betweenminds.presentation.question

import android.content.Context
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
import com.mmfsin.betweenminds.databinding.FragmentQuestionBinding
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.Score
import com.mmfsin.betweenminds.presentation.common.adapter.ScoreboardAdapter
import com.mmfsin.betweenminds.presentation.common.dialogs.EndGameDialog
import com.mmfsin.betweenminds.presentation.common.dialogs.save.SavePointsDialog
import com.mmfsin.betweenminds.utils.MODE_QUESTIONS
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyScoreList
import com.mmfsin.betweenminds.utils.getNumberColor
import com.mmfsin.betweenminds.utils.getPoints
import com.mmfsin.betweenminds.utils.handleAlpha
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.moveSliderValue
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
class QuestionFragment : BaseFragment<FragmentQuestionBinding, QuestionViewModel>() {

    override val viewModel: QuestionViewModel by viewModels()
    private lateinit var mContext: Context

    private var questions: List<Question> = emptyList()
    private var position = 0

    private var numberToGuess = 0f
    private var resultNumber = 0f
    private var round = 0

    private var scoreboardAdapter: ScoreboardAdapter? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentQuestionBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpScoreboard()
        viewModel.getQuestions()
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

            llBtnHide.animateY(500f, 1)
            llBtnCheck.animateY(500f, 1)
            rematch.root.animateX(500f, 1)

            initialStates()
        }
    }

    private fun initialStates() {
        binding.apply {
            hideCurtain()

            clQuestion.animateX(-1000f, 1)

            topSlider.moveSliderValue(0f)
            topSliderValue(0f)

            bottomSlider.isEnabled = false
            llBottomSlider.handleAlpha(0.4f, 350)
            bottomSlider.moveSliderValue(0f)
            bottomSliderValue(0f)

            topSlider.isEnabled = true
            btnHide.isEnabled = true
            btnCheck.isEnabled = true
            rematch.btnRematch.isEnabled = true
        }
    }

    override fun setListeners() {
        binding.apply {
            topSlider.addOnChangeListener { _, value, _ -> topSliderValue(value) }
            bottomSlider.addOnChangeListener { _, value, _ -> bottomSliderValue(value) }

            btnHide.setOnClickListener {
                btnHide.isEnabled = false
                topSlider.isEnabled = false
                showCurtain()
                llBtnHide.animateY(500f, 500)

                countDown(350) {
                    llBottomSlider.showAlpha(500) { bottomSlider.isEnabled = true }
                    llBtnCheck.animateY(0f, 500)
                }
            }

            btnCheck.setOnClickListener {
                btnCheck.isEnabled = false
                bottomSlider.isEnabled = false
                hideCurtain()
                setScoreRound()
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
                countDown(200) {
                    initialStates()
                    nextQuestion()
                }
            }
        }
    }

    private fun nextQuestion() {
        binding.apply {
            position++
            countDown(500) {
                if (position > questions.size - 1) position = 0
                tvQuestion.text = questions[position].text
                clQuestion.animateX(0f, 500)
                llBtnHide.animateY(0f, 500)
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is QuestionEvent.Questions -> setPhrases(event.phrases)
                is QuestionEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setPhrases(questions: List<Question>) {
        binding.apply {
            try {
                this@QuestionFragment.questions = questions
                tvQuestion.text = questions[position].text
                countDown(1000) {
                    loading.root.isVisible = false
                }
                countDown(1200) {
                    clQuestion.animateX(0f, 500)
                    llBtnHide.animateY(0f, 500)
                }
            } catch (e: Exception) {
                error()
            }
        }
    }

    private fun showCurtain() {
        binding.apply {
            tvTopNumber.text = "?"
            tvTopNumber.setTextColor(getColor(mContext, R.color.dark_grey))
            topSlider.hideAlpha(300)
        }
    }

    private fun hideCurtain() {
        binding.apply {
            topSliderValue(numberToGuess)
            topSlider.showAlpha(500)
        }
    }

    private fun topSliderValue(value: Float) {
        binding.apply {
            numberToGuess = value
            tvTopNumber.text = "${value.toInt().absoluteValue}"
            tvTopNumber.setTextColor(getColor(mContext, getNumberColor(value.toInt())))
        }
    }

    private fun bottomSliderValue(value: Float) {
        binding.apply {
            resultNumber = value

            tvBottomNumber.text = "${value.toInt().absoluteValue}"
            tvBottomNumber.setTextColor(getColor(mContext, getNumberColor(value.toInt())))

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

    private fun setScoreRound() {
        scoreboardAdapter?.updateScore(
            newScore = Score(
                discovered = true,
                topNumber = numberToGuess.toInt(),
                resultNumber = resultNumber.toInt(),
                points = getPoints(numberToGuess.toInt(), resultNumber.toInt())
            ), position = round
        )
    }

    private fun endGame() {
        val points = scoreboardAdapter?.getTotalPoints()
        points?.let {
            activity?.showFragmentDialog(
                EndGameDialog(points = points,
                    restartGame = { restartGame() },
                    saveScore = { showSaveScoreDialog(points) },
                    exit = { activity?.onBackPressedDispatcher?.onBackPressed() })
            )
        } ?: run { error() }
    }

    private fun showSaveScoreDialog(points: Int) {
        activity?.showFragmentDialog(
            SavePointsDialog(mode = MODE_QUESTIONS,
                points = points,
                restartGame = { restartGame() },
                exit = { activity?.onBackPressedDispatcher?.onBackPressed() })
        )
    }

    private fun restartGame() {
        round = 0
        scoreboardAdapter?.resetScores()
        initialStates()
        nextQuestion()
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

