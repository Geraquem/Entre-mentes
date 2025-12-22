package com.mmfsin.betweenminds.presentation.questions.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogNewNamesQuestionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewNamesQuestionsDialog(
    private val start: (blueName: String, orangeName: String) -> Unit,
    private val exit: () -> Unit,
) : BaseDialog<DialogNewNamesQuestionsBinding>() {

    override fun inflateView(inflater: LayoutInflater) =
        DialogNewNamesQuestionsBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = false
    }

    override fun setListeners() {
        binding.apply {
            btnStart.setOnClickListener {
                val blueName = etPlayerBlue.text.toString()
                val orangeName = etPlayerOrange.text.toString()
                start(blueName, orangeName)
                dismiss()
            }

            btnExit.setOnClickListener {
                exit()
                dismiss()
            }
        }
    }
}