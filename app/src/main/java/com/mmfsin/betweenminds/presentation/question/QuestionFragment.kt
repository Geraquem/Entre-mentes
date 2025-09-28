package com.mmfsin.betweenminds.presentation.question

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentQuestionBinding
import com.mmfsin.betweenminds.databinding.IncludeSliderQuestionsBinding
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.ScoreQuestion
import com.mmfsin.betweenminds.presentation.common.dialogs.EndGameDialog
import com.mmfsin.betweenminds.presentation.common.dialogs.save.SavePointsDialog
import com.mmfsin.betweenminds.presentation.question.adapter.ScoreboardQuestionAdapter
import com.mmfsin.betweenminds.utils.MODE_QUESTIONS
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyScoreQuestionList
import com.mmfsin.betweenminds.utils.getKonfettiParty
import com.mmfsin.betweenminds.utils.getPoints
import com.mmfsin.betweenminds.utils.handleAlpha
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.moveHumans
import com.mmfsin.betweenminds.utils.moveSliderValue
import com.mmfsin.betweenminds.utils.scaleHumans
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

    private var topLeftNumber = 50
    private var topRightNumber = 50
    private var bottomLeftNumber = 50
    private var bottomRightNumber = 50

    private var round = 0

    private var scoreboardAdapter: ScoreboardQuestionAdapter? = null

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
                scoreboardAdapter = ScoreboardQuestionAdapter(getEmptyScoreQuestionList())
                adapter = scoreboardAdapter
            }
        }
    }

    override fun setUI() {
        binding.apply {
//            (activity as BedRockActivity).setUpToolbar(
//                instructionsNavGraph = R.navigation.nav_graph_instr_questions
//            )

            loading.root.isVisible = true

            topSlider.apply {
                bgSlider.isEnabled = false
                slider.thumbTintList = ColorStateList.valueOf(WHITE)
                slider.haloRadius = 0
            }

            bottomSlider.apply {
                bgSlider.isEnabled = false
                slider.thumbTintList = ColorStateList.valueOf(WHITE)
                slider.haloRadius = 0
            }

            handleEditText(people.etPlayerBlue)
            handleEditText(people.etPlayerOrange)

            rlBtnHide.animateY(500f, 1)
            rlBtnCheck.animateY(500f, 1)
            rlBtnRematch.animateY(500f, 1)

            initialStates()
        }
    }

    private fun initialStates() {
        binding.apply {
            hideCurtain()
            scaleHumans(people, 50)

            tvQuestion.hideAlpha(1)

            topSlider.slider.moveSliderValue(50)

            bottomSlider.slider.isEnabled = false
            bottomSlider.root.handleAlpha(0.4f, 350)
            bottomSlider.slider.moveSliderValue(50)

            topSlider.slider.isEnabled = true
            btnHide.isEnabled = true
            btnCheck.isEnabled = true
            rematch.button.isEnabled = true
        }
    }

    private fun handleEditText(editText: EditText) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            editText.isCursorVisible = hasFocus
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == IME_ACTION_DONE) editText.clearFocus()
            false
        }
    }

    override fun setListeners() {
        binding.apply {
            topSlider.slider.addOnChangeListener { _, value, _ ->
                handleSliderValue(topSlider, value.toInt(), isTop = true)
            }
            bottomSlider.slider.addOnChangeListener { _, value, _ ->
                handleSliderValue(bottomSlider, value.toInt(), isTop = false)
            }

            btnHide.setOnClickListener {
                btnHide.isEnabled = false
                topSlider.slider.isEnabled = false

                showCurtain()
                scaleHumans(people, 50)
                moveHumans(people, 50)

                rlBtnHide.animateY(500f, 500)

                countDown(350) {
                    bottomSlider.root.showAlpha(500) { bottomSlider.slider.isEnabled = true }
                    rlBtnCheck.animateY(0f, 500)
                }
            }

            btnCheck.setOnClickListener {
                btnCheck.isEnabled = false
                bottomSlider.slider.isEnabled = false
                hideCurtain()
                setScoreRound()
                rlBtnCheck.animateY(500f, 500)

                countDown(500) {
                    /** Cuatro rondas 0,1,2,3 */
                    if (round > 2) countDown(1000) { endGame() }
                    else rlBtnRematch.animateY(0f, 500)
                }
            }

            rematch.button.setOnClickListener {
                round++
                rematch.button.isEnabled = false
                rlBtnRematch.animateY(500f, 500)
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
                tvQuestion.showAlpha(500)
                rlBtnHide.animateY(0f, 500)
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
                    tvQuestion.showAlpha(500)
                    rlBtnHide.animateY(0f, 500)
                }
            } catch (e: Exception) {
                error()
            }
        }
    }

    private fun showCurtain() = binding.curtain.root.showAlpha(350)
    private fun hideCurtain() = binding.curtain.root.hideAlpha(500)

    private fun handleSliderValue(
        slider: IncludeSliderQuestionsBinding,
        value: Int,
        isTop: Boolean
    ) {
        binding.apply {
            if (isTop) {
                topLeftNumber = value
                topRightNumber = 100 - value
            } else {
                bottomLeftNumber = value
                bottomRightNumber = 100 - value
            }

            val number1 = "${100 - value.absoluteValue}"
            val number2 = "${value.absoluteValue}"

            slider.tvPercentLeft.text = number2
            slider.tvPercentRight.text = number1

            slider.bgSlider.value = value.toFloat()

            people.etPlayerBlue.clearFocus()
            people.etPlayerOrange.clearFocus()

            scaleHumans(people, value)
            moveHumans(people, value)
        }
    }

    private fun setScoreRound() {
        val points = getPoints(topLeftNumber, bottomLeftNumber)
        if (points > 10) binding.konfetti.start(getKonfettiParty())

        scoreboardAdapter?.updateScore(
            newScore = ScoreQuestion(
                discovered = true,
                topNumber = Pair(topLeftNumber, topRightNumber),
                bottomNumber = Pair(bottomLeftNumber, bottomRightNumber),
                points = points
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

