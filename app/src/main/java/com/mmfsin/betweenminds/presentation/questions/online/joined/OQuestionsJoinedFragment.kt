package com.mmfsin.betweenminds.presentation.questions.online.joined

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
import com.mmfsin.betweenminds.domain.models.OnlineQuestionsAndNames
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.ScoreQuestion
import com.mmfsin.betweenminds.presentation.questions.adapter.ScoreboardQuestionAdapter
import com.mmfsin.betweenminds.presentation.questions.dialogs.EndQuestionsDialog
import com.mmfsin.betweenminds.presentation.questions.dialogs.OQuestionsJoinedStartDialog
import com.mmfsin.betweenminds.utils.BEDROCK_STR_ARGS
import com.mmfsin.betweenminds.utils.QUESTIONS_TYPE
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
import com.mmfsin.betweenminds.utils.waitingPartnerVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OQuestionsJoinedFragment :
    BaseFragment<FragmentQuestionsOnlineBinding, OQuestionsJoinedViewModel>() {

    override val viewModel: OQuestionsJoinedViewModel by viewModels()

    private lateinit var mContext: Context

    var roomId: String? = null

    private var gameNumber: Int = 1

    private var questionList: List<Question> = emptyList()
    private var serverData: OnlineQuestionsAndNames? = null
    private var position = 0
    private var round = 1
    private var myOpinion = 50
    private var myOpinionFloat = 50f

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
        roomId?.let { id ->
            activity?.showFragmentDialog(
                OQuestionsJoinedStartDialog(
                    close = { activity?.onBackPressedDispatcher?.onBackPressed() },
                    start = { viewModel.getQuestionsAndNames(id) },
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

            waitingPartnerVisibility(waiting, isVisible = false)

            roundNumber.text = "$round"
            people.apply {
                etPlayerBlue.isEnabled = false
                etPlayerOrange.isEnabled = false
            }

            setUpScoreboard()

            tvQuestion.hideAlpha(1)

            handlePercentsPlayerOne(people, show = false)
            handlePercentsPlayerTwo(people, show = true)

            buttonHide.button.text = getString(R.string.online_btn_compare)
            buttonNextRound.button.text = getString(R.string.btn_next_round)

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

                waitingPartnerVisibility(waiting, isVisible = true)
                roomId?.let { id -> viewModel.sendOpinionToRoom(id, round, myOpinionFloat) }
            }

            buttonNextRound.root.setOnClickListener {
                buttonNextRound.root.isEnabled = false
                buttonNextRound.root.animateY(500f, 500)

                if (round < 4) {
                    showRound { }
                    countDown(250) { nextRound() }
                } else nextRound()
            }

            val parent = secondOpinion.parent as View
            controller.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        people.etPlayerBlue.clearFocus()
                        people.etPlayerOrange.clearFocus()
                        secondOpinion.tag = event.rawX - secondOpinion.x
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val offsetX = secondOpinion.tag as Float
                        var newX = event.rawX - offsetX

                        if (newX < 0f) newX = 0f
                        if (newX + secondOpinion.width > parent.width) {
                            newX = (parent.width - secondOpinion.width).toFloat()
                        }
                        secondOpinion.x = newX
                        secondArrow.x =
                            secondOpinion.x + (secondOpinion.width - secondArrow.width) / 2f

                        val percentX =
                            ((secondOpinion.x / (parent.width - secondOpinion.width)) * 100)

                        myOpinion = percentX.toInt()
                        myOpinionFloat = percentX

                        updatePercents(people, 2, myOpinion)
                    }
                }
                true
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is OQuestionsJoinedEvent.GetQuestionsAndNames -> {
                    binding.loading.root.isVisible = false
                    serverData = event.data
                    questionList = event.data.questions
                    waitingPartnerVisibility(binding.waiting, isVisible = false)
                    showRound { setFirstPhase() }
                }

                is OQuestionsJoinedEvent.OtherPlayerOpinion -> {
                    binding.apply {
                        waitingPartnerVisibility(waiting, isVisible = false)
                        buttonNextRound.root.animateY(0f, 500)
                        checkPoints(event.otherOpinion.toInt())
                        moveOtherOpinionArrow(event.otherOpinion)
                        updatePercents(people, 1, event.otherOpinion.toInt())
                        handlePercentsPlayerOne(people, show = true)
                    }
                }

                is OQuestionsJoinedEvent.GameRestarted -> {
                    gameNumber = event.serverGameNumber
                    roomId?.let { id -> viewModel.getQuestionsAndNames(id) }
                }

                is OQuestionsJoinedEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setFirstPhase() {
        binding.apply {
            tvQuestion.text = questionList[position].text
            people.apply {
                etPlayerBlue.setText(serverData?.blueName)
                etPlayerOrange.setText(serverData?.orangeName)
            }
            tvQuestion.showAlpha(500)
            secondArrowVisibility(isVisible = true)
            curtainVisibility(isVisible = false)
            controller.isEnabled = true
            controllerInfo.root.handleAlpha(0.35f, 500)
            buttonHide.root.animateY(0f, 500)
        }
    }

    private fun nextRound() {
        binding.apply {
            round++
            position++

            roundNumber.text = "$round"
            tvQuestion.hideAlpha(350)

            curtainVisibility(isVisible = true) {
                firstArrow.translationX = 0f
                firstOpinion.translationX = 0f
                secondArrow.translationX = 0f
                secondOpinion.translationX = 0f
                myOpinion = 50

                people.apply {
                    percentOneBlue.text = getString(R.string.fifty)
                    percentTwoBlue.text = getString(R.string.fifty)
                    percentOneOrange.text = getString(R.string.fifty)
                    percentTwoOrange.text = getString(R.string.fifty)
                    moveHumans(this, 50)
                    handlePercentsPlayerOne(this, show = false)
                }
            }

            firstArrowVisibility(isVisible = false)
            secondArrowVisibility(isVisible = false)

            if (round > 3) buttonNextRound.button.text = getString(R.string.ranges_see_points)
            buttonHide.button.isEnabled = true
            buttonNextRound.button.isEnabled = true

            if (round >= 5) {
                round = 1
                roundNumber.text = "$round"

                position = 0
                endGame()

            } else countDown(1000) { setFirstPhase() }
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
                    topNumbers = Pair((100 - otherOpinion), otherOpinion),
                    bottomNumbers = Pair((100 - myOpinion), myOpinion),
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

    private fun moveOtherOpinionArrow(otherOpinion: Float) {
        binding.apply {
            val parent = firstOpinion.parent as View

            val clamped = otherOpinion.coerceIn(0f, 100f)

            val maxX = (parent.width - firstOpinion.width).toFloat()
            val newX = (clamped / 100f) * maxX

            firstOpinion.x = newX
            firstArrow.x = newX + (firstOpinion.width - firstArrow.width) / 2f

            firstArrowVisibility(isVisible = true)
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
        scoreboardQuestionAdapter?.resetScores()
        waitingPartnerVisibility(binding.waiting, isVisible = true)
        roomId?.let { id -> viewModel.waitCreatorToRestartGame(id, gameNumber) }
    }

    private fun showRound(onEnd: () -> Unit) {
        binding.apply {
            llRound.showAlpha(200) {
                countDown(750) { llRound.hideAlpha(500) { onEnd() } }
            }
        }
    }

    private fun openInstructions() = (activity as BedRockActivity).openBedRockActivity(
        navGraph = R.navigation.nav_graph_instructions,
        strArgs = QUESTIONS_TYPE
    )

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}