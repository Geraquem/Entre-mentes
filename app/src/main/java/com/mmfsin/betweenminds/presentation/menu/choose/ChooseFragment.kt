package com.mmfsin.betweenminds.presentation.menu.choose

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentChooseBinding
import com.mmfsin.betweenminds.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseFragment : BaseFragmentNoVM<FragmentChooseBinding>() {

    private lateinit var mContext: Context

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentChooseBinding.inflate(inflater, container, false)

    override fun setUI() {
        binding.apply {
            toolbar.btnInstructions.isVisible = false

            people.apply {
                etPlayerBlue.isEnabled = false
                etPlayerOrange.isEnabled = false
                etPlayerBlue.setText(getString(R.string.choose_questions_left_name))
                etPlayerOrange.setText(getString(R.string.choose_questions_right_name))
            }

            ranges.apply {
                tvRangeLeft.text = getString(R.string.choose_ranges_left)
                tvRangeRight.text = getString(R.string.choose_ranges_right)
            }
        }
    }

    override fun setListeners() {
        binding.apply {
            toolbar.btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }

            btnQuestions.setOnClickListener { navigateTo(R.navigation.nav_graph_questions) }
            btnRanges.setOnClickListener { navigateTo(R.navigation.nav_graph_ranges) }
        }
    }

    private fun navigateTo(navGraph: Int, strArgs: String? = null, booleanArgs: Boolean? = null) {
        (activity as MainActivity).openBedRockActivity(
            navGraph = navGraph, strArgs = strArgs, booleanArgs = booleanArgs
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}