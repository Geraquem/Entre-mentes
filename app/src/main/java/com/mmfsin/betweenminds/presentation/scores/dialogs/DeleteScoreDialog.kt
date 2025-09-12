package com.mmfsin.betweenminds.presentation.scores.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogDeleteScoreBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteScoreDialog(private val delete: () -> Unit) : BaseDialog<DialogDeleteScoreBinding>() {

    override fun inflateView(inflater: LayoutInflater) = DialogDeleteScoreBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = true
    }

    override fun setListeners() {
        binding.apply {
            btnCancel.setOnClickListener { dismiss() }

            btnDelete.setOnClickListener {
                delete()
                dismiss()
            }
        }
    }
}