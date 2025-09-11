package com.mmfsin.betweenminds.presentation.number.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogEndGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EndGameDialog(
    private val points: Int,
    private val restartGame: () -> Unit,
    private val exit: () -> Unit,
) : BaseDialog<DialogEndGameBinding>() {

    override fun inflateView(inflater: LayoutInflater) = DialogEndGameBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = false
        binding.apply {}
    }

    override fun setListeners() {
        binding.apply {
            btnExit.setOnClickListener {
                dismiss()
                exit()
            }
            btnRematch.setOnClickListener {
                dismiss()
                restartGame()
            }
        }
    }
}