package com.mmfsin.betweenminds.presentation.number.dialogs

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
            tvPoints.text = getString(R.string.endgame_pts, "$points")
            tvPhrase.text = getString(getPhrase())
        }
    }

    private fun getPhrase(): Int {
        return when (points) {
            in 0..16 -> R.string.endgame_phrase_bad
            in 17..39 -> R.string.endgame_phrase_medium_bad
            in 40..64 -> R.string.endgame_phrase_medium_good
            in 65..79 -> R.string.endgame_phrase_almost_perfect
            else -> R.string.endgame_phrase_perfect
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