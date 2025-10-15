package com.mmfsin.betweenminds.presentation.ranges.dialogs

import android.app.Dialog
import android.view.KeyEvent
import android.view.LayoutInflater
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogStartRangesOnlineBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ORangesStartDialog(
    private val close: () -> Unit,
    private val start: () -> Unit,
    private val instructions: () -> Unit,
) : BaseDialog<DialogStartRangesOnlineBinding>() {

    override fun inflateView(inflater: LayoutInflater) =
        DialogStartRangesOnlineBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = false
        binding.apply {
            btnStart.button.text = getString(R.string.ranges_start_play)
            btnInstructions.button.text = getString(R.string.ranges_start_instructions)
        }
    }

    override fun setListeners() {
        binding.apply {
            btnStart.root.setOnClickListener {
                start()
                dismiss()
            }

            btnInstructions.root.setOnClickListener { instructions() }

            dialog?.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    close()
                    true
                } else false
            }
        }
    }
}