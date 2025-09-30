package com.mmfsin.betweenminds.presentation.ranges.instructions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentInstrRangesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstrRangesFragment : BaseFragmentNoVM<FragmentInstrRangesBinding>() {

    private lateinit var mContext: Context

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentInstrRangesBinding.inflate(inflater, container, false)

    override fun setUI() {
        binding.apply {
            toolbar.btnInstructions.isVisible = false
            etClue.isEnabled = false
            ranges.apply {
                tvRangeLeft.text = getString(R.string.instr_ranges_example_left)
                tvRangeLeft.setLines(1)
                tvRangeRight.text = getString(R.string.instr_ranges_example_right)
                tvRangeRight.setLines(1)
            }

            ranges2.apply {
                tvRangeLeft.text = getString(R.string.instr_ranges_example_left)
                tvRangeLeft.setLines(1)
                tvRangeRight.text = getString(R.string.instr_ranges_example_right)
                tvRangeRight.setLines(1)
            }
        }
    }

    override fun setListeners() {
        binding.toolbar.btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

