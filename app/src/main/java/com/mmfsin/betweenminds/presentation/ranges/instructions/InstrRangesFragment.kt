package com.mmfsin.betweenminds.presentation.ranges.instructions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentInstrRangesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstrRangesFragment : BaseFragmentNoVM<FragmentInstrRangesBinding>() {

    private lateinit var mContext: Context

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentInstrRangesBinding.inflate(inflater, container, false)

    override fun setUI() {
        binding.apply {}
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

