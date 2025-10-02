package com.mmfsin.betweenminds.presentation.question

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.mmfsin.betweenminds.databinding.FragmentQuestionsBinding
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.ScoreQuestion
import com.mmfsin.betweenminds.presentation.question.adapter.ScoreboardQuestionAdapter
import com.mmfsin.betweenminds.presentation.question.dialogs.EndQuestionsDialog
import com.mmfsin.betweenminds.presentation.question.dialogs.QuestionsStartDialog
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyScoreQuestionList
import com.mmfsin.betweenminds.utils.getKonfettiParty
import com.mmfsin.betweenminds.utils.getQuestionModePoints
import com.mmfsin.betweenminds.utils.handleAlpha
import com.mmfsin.betweenminds.utils.handlePercentsPlayerOne
import com.mmfsin.betweenminds.utils.handlePercentsPlayerTwo
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.moveHumans
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import com.mmfsin.betweenminds.utils.updatePercents
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionsFragment :
    BaseFragment<FragmentQuestionsBinding, QuestionsViewModel>() {

    override val viewModel: QuestionsViewModel by viewModels()

    private lateinit var mContext: Context

    private var questionList: List<Question> = emptyList()
    private var position = 0
    private var round = 1
    private var phase = 1

    private var opinion1: Int? = null
    private var opinion2: Int? = null

    private var scoreboardQuestionAdapter: ScoreboardQuestionAdapter? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentQuestionsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpScoreboard()
        viewModel.getQuestions()
    }

    private fun setUpScoreboard() {
        binding.apply {
            scoreboard.rvScore.apply {
                layoutManager = GridLayoutManager(mContext, 4)
                scoreboardQuestionAdapter = ScoreboardQuestionAdapter(getEmptyScoreQuestionList())
                adapter = scoreboardQuestionAdapter
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
            handlePercentsPlayerTwo(people, show = false)

            handleEditText(people.etPlayerBlue)
            handleEditText(people.etPlayerOrange)

            controllerInfo.tvControllerText.text = getString(R.string.controller_limited)
            controllerInfo.root.hideAlpha(1)
            controller.isEnabled = false

            buttonHide.root.animateY(500f, 1)
            buttonCheck.root.animateY(500f, 1)
            buttonNextRound.root.animateY(500f, 1)

            firstArrowVisibility(isVisible = false)
            secondArrowVisibility(isVisible = false)
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
                        people.etPlayerBlue.clearFocus()
                        people.etPlayerOrange.clearFocus()
                        val view = if (phase == 1) firstOpinion else secondOpinion
                        view.tag = event.rawX - view.x
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val view = if (phase == 1) firstOpinion else secondOpinion
                        val arrow = if (phase == 1) firstArrow else secondArrow

                        val offsetX = view.tag as Float
                        var newX = event.rawX - offsetX

                        if (newX < 0f) newX = 0f
                        if (newX + view.width > parent.width) {
                            newX = (parent.width - view.width).toFloat()
                        }
                        view.x = newX
                        arrow.x = view.x + (view.width - arrow.width) / 2f

                        val percentX = ((view.x / (parent.width - view.width)) * 100).toInt()
                        updatePercents(people, phase, percentX)
                    }
                }
                true
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is QuestionsEvent.Questions -> {
                    binding.loading.root.isVisible = false
                    questionList = event.questions.shuffled()
                    showInitialDialog()
                }

                is QuestionsEvent.SomethingWentWrong -> error()
            }
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

    private fun showInitialDialog() {
        activity?.showFragmentDialog(
            QuestionsStartDialog(
                close = { activity?.onBackPressedDispatcher?.onBackPressed() },
                start = { blueName, orangeName ->
                    if (blueName.isNotEmpty()) binding.people.etPlayerBlue.setText(blueName)
                    if (orangeName.isNotEmpty()) binding.people.etPlayerOrange.setText(orangeName)
                    showRound { setFirstRanges() }
                },
                instructions = { openInstructions() }
            )
        )
    }

    private fun showRound(onEnd: () -> Unit) {
        binding.apply {
            llRound.showAlpha(500) {
                countDown(500) { llRound.hideAlpha(500) { onEnd() } }
            }
        }
    }

    private fun setFirstRanges() {
        binding.apply {
            tvQuestion.text = questionList[position].text
            countDown(500) { firstPhase() }
        }
    }

    private fun initialStates() {
        binding.apply {
            people.apply {
                percentOneBlue.text = getString(R.string.fifty)
                percentTwoBlue.text = getString(R.string.fifty)
                percentOneOrange.text = getString(R.string.fifty)
                percentTwoOrange.text = getString(R.string.fifty)
                moveHumans(this, 50)
                handlePercentsPlayerTwo(this, show = false)
            }

            firstArrowVisibility(isVisible = false)
            secondArrowVisibility(isVisible = false)

            firstArrow.translationX = 0f
            firstOpinion.translationX = 0f
            secondArrow.translationX = 0f
            secondOpinion.translationX = 0f

            buttonHide.root.isEnabled = true
            buttonCheck.root.isEnabled = true
            buttonNextRound.root.isEnabled = true

            if (round > 3) buttonNextRound.button.text = getString(R.string.ranges_see_points)
        }
    }

    private fun firstPhase() {
        binding.apply {
            phase = 1
            scoreboardQuestionAdapter?.roundColor(round - 1)
            countDown(500) {
                curtainVisibility(isVisible = false)
                tvQuestion.showAlpha(1000)
                firstArrowVisibility(isVisible = true)

                controller.isEnabled = true
                controllerInfoVisibility(true)

                buttonHide.root.animateY(0f, 1000)
            }
        }
    }

    private fun secondPhase() {
        binding.apply {
            phase = 2
            opinion1 = (people.percentOneBlue.text as String).toIntOrNull()

            handlePercentsPlayerOne(people, show = false)
            buttonHide.root.animateY(500f, 500)

            moveHumans(people, 50)

            firstArrow.hideAlpha(350)
            curtainVisibility(isVisible = true) {
                firstArrowVisibility(isVisible = false)
                handlePercentsPlayerTwo(people, show = true)
            }

            countDown(1500) {
                curtainVisibility(isVisible = false)
                secondArrowVisibility(isVisible = true)
                buttonCheck.root.animateY(0f, 500)
            }
        }
    }

    private fun thirdPhase() {
        binding.apply {
            opinion2 = (people.percentTwoBlue.text as String).toIntOrNull()
            controller.isEnabled = false
            controllerInfoVisibility(false)
            buttonCheck.root.animateY(500f, 500)

            firstArrowVisibility(isVisible = true)
            handlePercentsPlayerOne(people, show = true)

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

            position++
            curtainVisibility(isVisible = true) {
                handlePercentsPlayerTwo(people, show = false)
                initialStates()
            }

            if (position > questionList.size - 1) position = 0
            tvQuestion.hideAlpha(350) { tvQuestion.text = questionList[position].text }

            showRound { firstPhase() }
        }
    }

    private fun checkPoints() {
        binding.apply {
            val points = getQuestionModePoints(opinion1, opinion2)
            points?.let {
                if (points > 10) binding.konfetti.start(getKonfettiParty())

                val newScore = ScoreQuestion(
                    discovered = true,
                    actualQuestion = questionList[position].text,
                    topNumbers = Pair(opinion1, opinion1?.let { 100 - it }),
                    bottomNumbers = Pair(opinion2, opinion2?.let { 100 - it }),
                    points = points
                )

                scoreboardQuestionAdapter?.updateScore(
                    newScore = newScore,
                    position = round - 1
                )
            } ?: run { error() }
        }
    }

    private fun firstArrowVisibility(isVisible: Boolean) {
        binding.apply {
            if (isVisible) {
                firstOpinion.showAlpha(350)
                firstArrow.showAlpha(350)
            } else {
                firstOpinion.hideAlpha(10)
                firstArrow.hideAlpha(100)
            }
        }
    }

    private fun secondArrowVisibility(isVisible: Boolean) {
        binding.apply {
            val view = if (isVisible) View.VISIBLE else View.INVISIBLE
            secondOpinion.visibility = view
            if (isVisible) secondArrow.showAlpha(350) else secondArrow.hideAlpha(10)
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

    private fun controllerInfoVisibility(isVisible: Boolean) {
        val cInfo = if (_binding != null) binding.controllerInfo.root else null
        if (isVisible) cInfo?.handleAlpha(0.35f, 350)
        else cInfo?.hideAlpha(350)
    }

    private fun endGame() {
        endGameStates()
        val data = scoreboardQuestionAdapter?.getTotalData()
        data?.let {
            activity?.showFragmentDialog(
                EndQuestionsDialog(
                    data = data,
                    restartGame = { restartGame() },
                    exit = { activity?.onBackPressedDispatcher?.onBackPressed() }
                )
            )
        } ?: run { error() }
    }

    private fun endGameStates() {
        binding.apply {
            curtainVisibility(isVisible = true)
            tvQuestion.hideAlpha(350) { initialStates() }
        }
    }

    private fun restartGame() {
        round = 1
        binding.roundNumber.text = "$round"
        position++
        scoreboardQuestionAdapter?.resetScores()

        showRound { setFirstRanges() }
    }

    private fun openInstructions() =
        (activity as BedRockActivity).openBedRockActivity(R.navigation.nav_graph_instr_questions)

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}