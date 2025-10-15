package com.mmfsin.betweenminds.presentation.questions.online.creator

import android.annotation.SuppressLint
import android.content.Context
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
import com.mmfsin.betweenminds.databinding.FragmentQuestionsOnlineBinding
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.ScoreQuestion
import com.mmfsin.betweenminds.presentation.common.dialog.WaitingOtherPlayerDialog
import com.mmfsin.betweenminds.presentation.questions.adapter.ScoreboardQuestionAdapter
import com.mmfsin.betweenminds.presentation.questions.dialogs.EndQuestionsDialog
import com.mmfsin.betweenminds.presentation.questions.dialogs.OQuestionsCreatorStartDialog
import com.mmfsin.betweenminds.utils.BEDROCK_STR_ARGS
import com.mmfsin.betweenminds.utils.QUESTIONS_TYPE
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.checkNotNulls
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyScoreQuestionList
import com.mmfsin.betweenminds.utils.getKonfettiParty
import com.mmfsin.betweenminds.utils.getQuestionModePoints
import com.mmfsin.betweenminds.utils.handlePercentsPlayerTwo
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.moveHumans
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import com.mmfsin.betweenminds.utils.updatePercents
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OQuestionsCreatorFragment :
    BaseFragment<FragmentQuestionsOnlineBinding, OQuestionsCreatorViewModel>() {

    override val viewModel: OQuestionsCreatorViewModel by viewModels()

    private lateinit var mContext: Context

    private var blueName: String? = null
    private var orangeName: String? = null

    var roomId: String? = null

    private var gameNumber: Int = 1

    private var questionList: List<Question> = emptyList()
    private var position = 0
    private var questionPosition = 0
    private var round = 1
    private var myOpinion = 50

    private var waitingDialog: WaitingOtherPlayerDialog? = null

    private var scoreboardQuestionAdapter: ScoreboardQuestionAdapter? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentQuestionsOnlineBinding.inflate(inflater, container, false)

    override fun getBundleArgs() {
        activity?.intent?.apply {
            roomId = getStringExtra(BEDROCK_STR_ARGS)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomId?.let {
            activity?.showFragmentDialog(
                OQuestionsCreatorStartDialog(
                    close = { activity?.onBackPressedDispatcher?.onBackPressed() },
                    start = { bName, oName ->
                        blueName = bName
                        orangeName = oName
                        viewModel.getQuestions()
                    },
                    instructions = { openInstructions() }
                )
            )
        } ?: run { error() }
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

            roundNumber.text = "$round"
            people.apply {
                etPlayerBlue.isEnabled = false
                etPlayerOrange.isEnabled = false
            }

            setUpScoreboard()

            buttonHide.button.text = getString(R.string.online_btn_compare)
            buttonCheck.button.text = getString(R.string.btn_check)
            buttonNextRound.button.text = getString(R.string.btn_next_round)

            tvQuestion.hideAlpha(1)
            handlePercentsPlayerTwo(people, show = false)

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

            buttonHide.button.setOnClickListener {
                buttonHide.button.isEnabled = false
                buttonHide.root.animateY(500f, 500)
                controller.isEnabled = false
                binding.controllerInfo.root.hideAlpha(350)

                waitingDialog = WaitingOtherPlayerDialog()
                waitingDialog?.let { d -> activity?.showFragmentDialog(d) }
                roomId?.let { id -> viewModel.sendOpinionToRoom(id, round, myOpinion) }
            }

            buttonCheck.button.setOnClickListener {
                buttonCheck.button.isEnabled = false
            }

            buttonNextRound.root.setOnClickListener {
                buttonNextRound.root.isEnabled = false
                buttonNextRound.root.animateY(500f, 500)

                if (round < 4) {
                    showRound { }
                    countDown(250) { nextRound() }
                } else nextRound()
            }

            val parent = firstOpinion.parent as View
            controller.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        people.etPlayerBlue.clearFocus()
                        people.etPlayerOrange.clearFocus()
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
                        firstArrow.x = firstOpinion.x + (firstOpinion.width - firstArrow.width) / 2f

                        val percentX =
                            ((firstOpinion.x / (parent.width - firstOpinion.width)) * 100).toInt()
                        myOpinion = percentX
                        updatePercents(people, 1, percentX)
                    }
                }
                true
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is OQuestionsCreatorEvent.GetQuestions -> {
                    questionList = event.questions.shuffled()
                    roomId?.let { id ->
                        checkNotNulls(blueName, orangeName) { bN, oN ->
                            binding.people.apply {
                                etPlayerBlue.setText(bN)
                                etPlayerOrange.setText(oN)
                            }
                            viewModel.setQuestionsInRoom(
                                roomId = id,
                                names = Pair(bN, oN),
                                questions = getQuestionsToRoom(),
                                gameNumber = gameNumber
                            )
                        }
                    }
                }

                is OQuestionsCreatorEvent.QuestionsSetInRoom -> {
                    binding.loading.root.isVisible = false
                    showRound { setFirstPhase() }
                }

                is OQuestionsCreatorEvent.GameRestarted -> {
                    roomId?.let { id ->
                        viewModel.updateQuestions(
                            roomId = id,
                            questions = getQuestionsToRoom(),
                            gameNumber = gameNumber
                        )
                    }
                }

                is OQuestionsCreatorEvent.OtherPlayerOpinion -> {
                    waitingDialog?.dismiss()
                    binding.buttonNextRound.root.animateY(0f, 500)
                    checkPoints(event.otherOpinion)
                    moveOtherOpinionArrow(event.otherOpinion)
                    updatePercents(binding.people, 2, event.otherOpinion)
                    handlePercentsPlayerTwo(binding.people, show = true)
                }

                is OQuestionsCreatorEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun getQuestionsToRoom(): List<Question> {
        if (questionPosition >= questionList.size) error()

        val result = questionList.subList(
            questionPosition, (questionPosition + 4).coerceAtMost(questionList.size)
        )
        questionPosition += 4
        return result
    }

    private fun setFirstPhase() {
        binding.apply {
            if (position >= questionList.size) position = 0
            tvQuestion.text = questionList[position].text
            tvQuestion.showAlpha(500)
            firstArrowVisibility(isVisible = true)
            curtainVisibility(isVisible = false)
            controller.isEnabled = true
            controllerInfo.root.showAlpha(500)
            buttonHide.root.animateY(0f, 500)
        }
    }

    private fun nextRound() {
        binding.apply {
            round++
            position++

            roundNumber.text = "$round"
            tvQuestion.hideAlpha(350)
            curtainVisibility(isVisible = true)
            firstArrowVisibility(isVisible = false)
            secondArrowVisibility(isVisible = false)
            if (round > 3) buttonNextRound.button.text = getString(R.string.ranges_see_points)
            buttonHide.button.isEnabled = true
            buttonNextRound.button.isEnabled = true

            people.apply {
                percentOneBlue.text = getString(R.string.fifty)
                percentTwoBlue.text = getString(R.string.fifty)
                percentOneOrange.text = getString(R.string.fifty)
                percentTwoOrange.text = getString(R.string.fifty)
                moveHumans(this, 50)
                handlePercentsPlayerTwo(this, show = false)
            }

            firstArrow.translationX = 0f
            firstOpinion.translationX = 0f
            secondArrow.translationX = 0f
            secondOpinion.translationX = 0f
            myOpinion = 50

            if (round >= 5) {
                round = 1
                roundNumber.text = "$round"
                endGame()

            } else countDown(1500) { setFirstPhase() }
        }
    }

    private fun checkPoints(otherOpinion: Int) {
        binding.apply {
            val points = getQuestionModePoints(myOpinion, otherOpinion)
            points?.let {
                if (points > 10) binding.konfetti.start(getKonfettiParty())

                val newScore = ScoreQuestion(
                    discovered = true,
                    actualQuestion = questionList[position].text,
                    topNumbers = Pair((100 - myOpinion), myOpinion),
                    bottomNumbers = Pair((100 - otherOpinion), otherOpinion),
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
                curtainLeft.animateX(0f, 500) { onEnd() }
                curtainRight.animateX(0f, 500) { onEnd() }
            } else {
                curtainLeft.animateX(-1000f, 1000) { onEnd() }
                curtainRight.animateX(1000f, 1000) { onEnd() }
            }
        }
    }

    private fun moveOtherOpinionArrow(otherOpinion: Int) {
        binding.apply {
            val parent = secondOpinion.parent as View

            val clamped = otherOpinion.coerceIn(0, 100)
            val maxX = (parent.width - secondOpinion.width).toFloat()
            val newX = (clamped / 100f) * maxX

            secondOpinion.x = newX
            secondArrow.x = newX + (secondOpinion.width - secondArrow.width) / 2f
            secondArrowVisibility(isVisible = true)
        }
    }

    private fun endGame() {
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

    private fun restartGame() {
        gameNumber++
        scoreboardQuestionAdapter?.resetScores()
        binding.loading.root.isVisible = true
        roomId?.let { id -> viewModel.restartGame(id) }
    }

    private fun openInstructions() = (activity as BedRockActivity).openBedRockActivity(
        navGraph = R.navigation.nav_graph_instructions,
        strArgs = QUESTIONS_TYPE
    )

    private fun showRound(onEnd: () -> Unit) {
        binding.apply {
            llRound.showAlpha(200) {
                countDown(750) { llRound.hideAlpha(500) { onEnd() } }
            }
        }
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}