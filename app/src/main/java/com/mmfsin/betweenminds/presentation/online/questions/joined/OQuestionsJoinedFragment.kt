package com.mmfsin.betweenminds.presentation.online.questions.joined

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
import com.mmfsin.betweenminds.presentation.online.common.dialog.WaitingOtherPlayerDialog
import com.mmfsin.betweenminds.presentation.question.adapter.ScoreboardQuestionAdapter
import com.mmfsin.betweenminds.utils.BEDROCK_STR_ARGS
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.getEmptyScoreQuestionList
import com.mmfsin.betweenminds.utils.handlePercentsPlayerOne
import com.mmfsin.betweenminds.utils.handlePercentsPlayerTwo
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import com.mmfsin.betweenminds.utils.updatePercents
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OQuestionsJoinedFragment :
    BaseFragment<FragmentQuestionsOnlineBinding, OQuestionsJoinedViewModel>() {

    override val viewModel: OQuestionsJoinedViewModel by viewModels()

    private lateinit var mContext: Context

    var roomId: String? = null

    private var questionList: List<Question> = emptyList()
    private var position = 0
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
        roomId?.let { id -> viewModel.getQuestionsAndNames(id) } ?: run { error() }
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
            llRound.isVisible = false
            people.apply {
                etPlayerBlue.isEnabled = false
                etPlayerOrange.isEnabled = false
            }
            firstOpinion.isVisible = false
            firstArrow.isVisible = false

            setUpScoreboard()

            tvQuestion.hideAlpha(1)

            handlePercentsPlayerOne(people, show = false)
            handlePercentsPlayerTwo(people, show = true)

            buttonHide.button.text = getString(R.string.online_btn_save_answer)
            buttonCheck.button.text = getString(R.string.btn_check)
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
                            ((secondOpinion.x / (parent.width - secondOpinion.width)) * 100).toInt()
                        myOpinion = percentX
                        updatePercents(people, 2, percentX)
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
                    setFirstPhase(event.data)
                }

                is OQuestionsJoinedEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setFirstPhase(data: OnlineQuestionsAndNames) {
        binding.apply {
            tvQuestion.text = data.questions[position].text
            people.apply {
                etPlayerBlue.setText(data.blueName)
                etPlayerOrange.setText(data.orangeName)
            }
            tvQuestion.showAlpha(500)
            secondArrowVisibility(isVisible = true)
            curtainVisibility(isVisible = false)
            controller.isEnabled = true
            controllerInfo.root.showAlpha(500)
            buttonHide.root.animateY(0f, 500)
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

    private fun openInstructions() =
        (activity as BedRockActivity).openBedRockActivity(R.navigation.nav_graph_instr_questions)

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}