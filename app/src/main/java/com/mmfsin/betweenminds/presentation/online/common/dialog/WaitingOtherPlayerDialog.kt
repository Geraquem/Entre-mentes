package com.mmfsin.betweenminds.presentation.online.common.dialog

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogWaitingOtherPlayerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaitingOtherPlayerDialog(
) : BaseDialog<DialogWaitingOtherPlayerBinding>() {

    override fun inflateView(inflater: LayoutInflater) =
        DialogWaitingOtherPlayerBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = false
    }
}