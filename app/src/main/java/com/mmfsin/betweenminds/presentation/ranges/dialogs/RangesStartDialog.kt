package com.mmfsin.betweenminds.presentation.ranges.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogStartRangesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RangesStartDialog(
    private val start: () -> Unit,
    private val instructions: () -> Unit,
) : BaseDialog<DialogStartRangesBinding>() {

    override fun inflateView(inflater: LayoutInflater) = DialogStartRangesBinding.inflate(inflater)

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
        }
    }
}