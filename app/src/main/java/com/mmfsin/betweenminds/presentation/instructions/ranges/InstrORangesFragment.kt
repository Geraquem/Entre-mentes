package com.mmfsin.betweenminds.presentation.instructions.ranges

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentInstrRangesBinding
import com.mmfsin.betweenminds.databinding.FragmentInstrRangesOnlineBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstrORangesFragment : BaseFragmentNoVM<FragmentInstrRangesOnlineBinding>() {

    private lateinit var mContext: Context

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentInstrRangesOnlineBinding.inflate(inflater, container, false)

    override fun setUI() {
        binding.apply {
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

