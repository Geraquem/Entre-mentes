package com.mmfsin.betweenminds.presentation.common.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogEndGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EndGameDialog(
    private val points: Int,
    private val restartGame: () -> Unit,
    private val saveScore: () -> Unit,
    private val exit: () -> Unit,
) : BaseDialog<DialogEndGameBinding>() {

    override fun inflateView(inflater: LayoutInflater) = DialogEndGameBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = false
        binding.apply {
            tvPoints.text = if (points == 1) getString(R.string.endgame_one_point)
            else getString(R.string.endgame_pts, "$points")

            tvPhrase.text = getString(getPhrase())
        }
    }

    private fun getPhrase(): Int {

        return when (points) {
            80 -> R.string.endgame_phrase_perfect
            in 60..79 -> R.string.endgame_phrase_one
            in 38..59 -> R.string.endgame_phrase_two
            in 16..37 -> R.string.endgame_phrase_three
            in 5..15 -> R.string.endgame_phrase_four
            else -> R.string.endgame_phrase_five
        }
    }

    override fun setListeners() {
        binding.apply {
            btnExit.setOnClickListener {
                exit()
                dismiss()
            }

            btnSave.setOnClickListener {
                saveScore()
                dismiss()
            }

            btnRematch.setOnClickListener {
                restartGame()
                dismiss()
            }
        }
    }
}