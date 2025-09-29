package com.mmfsin.betweenminds.presentation.common.dialogs

import android.app.Dialog
import android.view.LayoutInflater
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.FragmentBlankBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EndGameDialog(
    private val points: Int,
    private val restartGame: () -> Unit,
    private val saveScore: () -> Unit,
    private val exit: () -> Unit,
) : BaseDialog<FragmentBlankBinding>() {

    override fun inflateView(inflater: LayoutInflater) = FragmentBlankBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun setUI() {
        isCancelable = false
        binding.apply {
        }
    }
}