package com.mmfsin.betweenminds.presentation.choose.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogExitGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExitGameDialog(val exit: () -> Unit) : BaseDialog<DialogExitGameBinding>() {

    override fun inflateView(inflater: LayoutInflater) =
        DialogExitGameBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = true
        binding.btnExit.text = getString(R.string.exit)
    }

    override fun setListeners() {
        binding.apply {
            btnExit.setOnClickListener {
                exit()
                dismiss()
            }

            btnStay.setOnClickListener { dismiss() }
        }
    }
}