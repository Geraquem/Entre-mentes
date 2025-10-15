package com.mmfsin.betweenminds.presentation.questions.dialogs

import android.app.Dialog
import android.view.KeyEvent
import android.view.LayoutInflater
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogStartQuestionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionsStartDialog(
    private val close: () -> Unit,
    private val start: (blueName: String, orangeName: String) -> Unit,
    private val instructions: () -> Unit,
) : BaseDialog<DialogStartQuestionsBinding>() {

    override fun inflateView(inflater: LayoutInflater) =
        DialogStartQuestionsBinding.inflate(inflater)

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
                val blueName = etPlayerBlue.text.toString()
                val orangeName = etPlayerOrange.text.toString()
                start(blueName, orangeName)
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