package com.mmfsin.betweenminds.presentation.menu.dialogs

import android.view.LayoutInflater
import com.mmfsin.betweenminds.base.BaseBottomSheet
import com.mmfsin.betweenminds.databinding.SheetSelectorBinding
import com.mmfsin.betweenminds.presentation.menu.interfaces.ISelectorListener

class SelectorSheet(private val listener: ISelectorListener) :
    BaseBottomSheet<SheetSelectorBinding>() {

    override fun inflateView(inflater: LayoutInflater) = SheetSelectorBinding.inflate(inflater)

    override fun setListeners() {
        binding.apply {
            btnQuestionsInstr.setOnClickListener { listener.openQuestionsInstructions() }
            btnQuestions.setOnClickListener {
                listener.openQuestionsMode()
                dismiss()
            }

            btnRangesInstr.setOnClickListener { listener.openRangesInstructions() }
            btnRanges.setOnClickListener {
                listener.openRangesMode()
                dismiss()
            }
        }
    }
}