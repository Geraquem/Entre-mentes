package com.mmfsin.betweenminds.presentation.online.ranges.creator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentRangesOnlineCreatorBinding
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ORangesCreatorFragment :
    BaseFragment<FragmentRangesOnlineCreatorBinding, ORangesCreatorViewModel>() {

    override val viewModel: ORangesCreatorViewModel by viewModels()

    private lateinit var mContext: Context


    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRangesOnlineCreatorBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setUI() {
        binding.apply {}
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setListeners() {
        binding.apply {
            toolbar.apply {
                btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
                btnInstructions.setOnClickListener { openInstructions() }
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is ORangesCreatorEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun openInstructions() =
        (activity as BedRockActivity).openBedRockActivity(R.navigation.nav_graph_instr_ranges)

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}