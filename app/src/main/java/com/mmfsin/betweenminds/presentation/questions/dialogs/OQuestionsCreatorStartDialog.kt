package com.mmfsin.betweenminds.presentation.questions.dialogs

import android.app.Dialog
import android.view.KeyEvent
import android.view.LayoutInflater
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogStartQuestionsOnlineCreatorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OQuestionsCreatorStartDialog(
    private val close: () -> Unit,
    private val start: (blueName: String, orangeName: String) -> Unit,
    private val instructions: () -> Unit,
) : BaseDialog<DialogStartQuestionsOnlineCreatorBinding>() {

    override fun inflateView(inflater: LayoutInflater) =
        DialogStartQuestionsOnlineCreatorBinding.inflate(inflater)

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
                var blueName = etPlayerBlue.text.toString()
                var orangeName = etPlayerOrange.text.toString()

                blueName = blueName.ifEmpty { getString(R.string.questions_blue_player) }
                orangeName = orangeName.ifEmpty { getString(R.string.questions_orange_player) }

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