package com.mmfsin.betweenminds.presentation.choose.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogErrorJoiningRoomBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotAbleToJoinDialog(
) : BaseDialog<DialogErrorJoiningRoomBinding>() {

    override fun inflateView(inflater: LayoutInflater) =
        DialogErrorJoiningRoomBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = true
        binding.btnAccept.button.text = getString(R.string.error_btn)
    }

    override fun setListeners() {
        binding.apply {
            btnAccept.button.setOnClickListener { dismiss() }
        }
    }
}