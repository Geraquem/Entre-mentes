package com.mmfsin.betweenminds.presentation.online.ranges.creator.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogEndRangesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateRoomDialog(
    private val points: Int,
    private val restartGame: () -> Unit,
    private val exit: () -> Unit,
) : BaseDialog<DialogEndRangesBinding>() {

    override fun inflateView(inflater: LayoutInflater) = DialogEndRangesBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = false
        binding.apply {
            tvPoints.text = if (points == 1) getString(R.string.endgame_one_point)
            else getString(R.string.endgame_pts, "$points")

            tvPhrase.text = getString(getPhrase(points))
        }
    }

    private fun getPhrase(points: Int): Int {
        return when (points) {
            20 -> R.string.endgame_phrase_perfect
            in 16..17 -> R.string.endgame_phrase_one
            in 10..15 -> R.string.endgame_phrase_two
            in 5..9 -> R.string.endgame_phrase_three
            in 1..4 -> R.string.endgame_phrase_four
            else -> R.string.endgame_phrase_five
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