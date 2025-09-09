package com.mmfsin.betweenminds.base.dialog

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogErrorBinding

class ErrorDialog(private val goBack: Boolean = true) : BaseDialog<DialogErrorBinding>() {

    override fun inflateView(inflater: LayoutInflater) = DialogErrorBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setListeners() {
        binding.btnAccept.setOnClickListener {
            if (goBack) activity?.onBackPressedDispatcher?.onBackPressed()
            dismiss()
        }
    }
}