package com.mmfsin.betweenminds.presentation.ranges.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import androidx.core.content.ContextCompat.getColor
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogEndRangesOnlineBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EndGameORangesDialog(
    private val myPoints: Int,
    private val otherPlayerPoints: Int,
    val exit: () -> Unit,
    val replay: () -> Unit,
) : BaseDialog<DialogEndRangesOnlineBinding>() {

    override fun inflateView(inflater: LayoutInflater) =
        DialogEndRangesOnlineBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = false
        binding.apply {
            tvMyPoints.text = if (myPoints == 1) getString(R.string.endgame_one_point)
            else getString(R.string.endgame_pts, "$myPoints")

            tvOtherPoints.text = if (otherPlayerPoints == 1) getString(R.string.endgame_one_point)
            else getString(R.string.endgame_pts, "$otherPlayerPoints")

            val total = myPoints + otherPlayerPoints
            tvTotalPoints.text = if (total == 1) getString(R.string.endgame_one_point)
            else getString(R.string.endgame_pts, "$total")

            tvPhrase.text = getString(getPhrase(total))
            setTvPointsColor(total)
        }
    }

    private fun getPhrase(points: Int): Int {
        return when (points) {
            30 -> R.string.endgame_phrase_perfect
            in 22..29 -> R.string.endgame_phrase_one
            in 16..21 -> R.string.endgame_phrase_two
            in 8..15 -> R.string.endgame_phrase_three
            in 1..7 -> R.string.endgame_phrase_four
            else -> R.string.endgame_phrase_five
        }
    }

    private fun setTvPointsColor(points: Int) {
        val color = when (points) {
            in 22..30 -> R.color.dark_green
            in 8..21 -> R.color.dark_orange
            in 1..7 -> R.color.dark_red
            else -> R.color.black
        }
        context?.let { c -> binding.tvTotalPoints.setTextColor(getColor(c, color)) }
    }

    override fun setListeners() {
        binding.apply {
            btnExit.setOnClickListener {
                exit()
                dismiss()
            }

            btnRematch.setOnClickListener {
                replay()
                dismiss()
            }
        }
    }
}