package com.mmfsin.betweenminds.presentation.online.ranges.creator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentRangesOnlineCreatorBinding
import com.mmfsin.betweenminds.domain.models.OnlineData
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.presentation.online.common.dialog.WaitingOtherPlayerDialog
import com.mmfsin.betweenminds.presentation.ranges.adapter.ScoreboardRangesAdapter
import com.mmfsin.betweenminds.utils.BEDROCK_STR_ARGS
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getEmptyScoreRangesList
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ORangesCreatorFragment :
    BaseFragment<FragmentRangesOnlineCreatorBinding, ORangesCreatorViewModel>() {

    override val viewModel: ORangesCreatorViewModel by viewModels()

    private lateinit var mContext: Context

    var roomId: String? = null

    private var rangesList: List<Range> = emptyList()
    private var position = 0
    private var bullseyePosition = 0
    private var round = 1

    private var scoreboardRangesAdapter: ScoreboardRangesAdapter? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRangesOnlineCreatorBinding.inflate(inflater, container, false)

    override fun getBundleArgs() {
        roomId = activity?.intent?.getStringExtra(BEDROCK_STR_ARGS)
    }

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
            buttonHide.button.text = getString(R.string.btn_hide)
            buttonCheck.button.text = getString(R.string.btn_check)
            buttonNextRound.button.text = getString(R.string.btn_next_round)

            roundNumber.text = "$round"

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

            buttonHide.button.setOnClickListener {
                roomId?.let { id ->
                    val data = OnlineData(
                        roomId = id,
                        round = round,
                        isCreator = true,
                        bullseyePosition = bullseyePosition,
                        hint = etClue.text.toString(),
                        leftRange = rangesList[position].leftRange,
                        rightRange = rangesList[position].rightRange
                    )
                    viewModel.sendDataToOtherPlayer(data)
                    activity?.showFragmentDialog(WaitingOtherPlayerDialog())
                }
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is ORangesCreatorEvent.GetRanges -> {
                    rangesList = event.ranges.shuffled()
                    setRangesUI()
                }

                is ORangesCreatorEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setRangesUI() {
        binding.apply {
            val actualRange = rangesList[position]
            ranges.tvRangeLeft.text = actualRange.leftRange
            ranges.tvRangeRight.text = actualRange.rightRange
            setBullsEye()
            countDown(500) {
                buttonHide.root.animateY(0f, 350)
                curtainVisibility(isVisible = false)
                llRound.hideAlpha(1000) {
                    ranges.apply {
                        tvRangeLeft.showAlpha(350)
                        tvRangeRight.showAlpha(350)
                    }
                }
            }
        }
    }

    private fun setBullsEye() {
        binding.apply {
            val parent = rlSlider
            val child = bullsEye.root
            parent.post {
                val parentWidth = parent.width
                val bullseyeWidth = child.width

                val centerOffset = (0.2f * bullseyeWidth) + (0.35f * bullseyeWidth) / 2f

                val minX = -centerOffset
                val maxX = parentWidth - (bullseyeWidth - centerOffset)

                val randomX = (minX.toInt()..maxX.toInt()).random()
                child.x = randomX.toFloat()
//                child.x = minX
            }
        }
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
                curtainLeft.animateX(0f, 1000) { onEnd() }
                curtainRight.animateX(0f, 1000) { onEnd() }
            } else {
                curtainLeft.animateX(-1000f, 1000) { onEnd() }
                curtainRight.animateX(1000f, 1000) { onEnd() }
            }
        }
    }

    private fun openInstructions() =
        (activity as BedRockActivity).openBedRockActivity(R.navigation.nav_graph_instr_ranges)

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}