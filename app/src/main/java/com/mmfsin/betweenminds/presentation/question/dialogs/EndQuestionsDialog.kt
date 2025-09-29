package com.mmfsin.betweenminds.presentation.question.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogEndQuestionsBinding
import com.mmfsin.betweenminds.domain.models.ScoreQuestion
import com.mmfsin.betweenminds.presentation.question.adapter.QuestionAnswersAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EndQuestionsDialog(
    private val data: List<ScoreQuestion>,
    private val restartGame: () -> Unit,
    private val exit: () -> Unit,
) : BaseDialog<DialogEndQuestionsBinding>() {

    override fun inflateView(inflater: LayoutInflater) = DialogEndQuestionsBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = false
        setUpRvAnswers()
        val points = getTotalPoints()
        binding.apply {
            tvPoints.text = if (points == 1) getString(R.string.endgame_one_point)
            else getString(R.string.endgame_pts, "$points")
        }
    }

    private fun getTotalPoints(): Int {
        var totalPoints = 0
        data.forEach {
            it.points?.let { points -> totalPoints += points }
        }
        return totalPoints
    }

    private fun getPhrase(points: Int): Int {
        return when (points) {
            80 -> R.string.endgame_phrase_perfect
            in 60..79 -> R.string.endgame_phrase_one
            in 38..59 -> R.string.endgame_phrase_two
            in 16..37 -> R.string.endgame_phrase_three
            in 5..15 -> R.string.endgame_phrase_four
            else -> R.string.endgame_phrase_five
        }
    }

    private fun setUpRvAnswers() {
        binding.rvAnswers.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = QuestionAnswersAdapter(data)
        }
    }

    override fun setListeners() {
        binding.apply {
            btnExit.setOnClickListener {
                exit()
                dismiss()
            }

            btnRematch.setOnClickListener {
                restartGame()
                dismiss()
            }
        }
    }
}