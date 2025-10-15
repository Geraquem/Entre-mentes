package com.mmfsin.betweenminds.presentation.instructions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayoutMediator
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentInstructionsVpBinding
import com.mmfsin.betweenminds.presentation.instructions.adapter.ViewPagerInstrAdapter
import com.mmfsin.betweenminds.utils.BEDROCK_BOOLEAN_ARGS
import com.mmfsin.betweenminds.utils.BEDROCK_STR_ARGS
import com.mmfsin.betweenminds.utils.QUESTIONS_TYPE
import com.mmfsin.betweenminds.utils.RANGES_TYPE
import com.mmfsin.betweenminds.utils.checkNotNulls
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstructionsVPFragment : BaseFragmentNoVM<FragmentInstructionsVpBinding>() {

    private lateinit var mContext: Context

    private var gameType: String? = null
    private var onlineModeFirst: Boolean = true

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentInstructionsVpBinding.inflate(inflater, container, false)

    override fun getBundleArgs() {
        activity?.intent?.let {
            gameType = it.getStringExtra(BEDROCK_STR_ARGS)
            onlineModeFirst = it.getBooleanExtra(BEDROCK_BOOLEAN_ARGS, true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
    }

    override fun setUI() {
        val type = when (gameType) {
            QUESTIONS_TYPE -> R.string.instr_mode_questions
            RANGES_TYPE -> R.string.instr_mode_ranges
            else -> R.string.error_title
        }
        binding.tvType.text = getString(type)
    }

    override fun setListeners() {
        binding.toolbar.apply {
            btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
            btnInstructions.isVisible = false
        }
    }

    private fun setUpViewPager() {
        binding.apply {
            checkNotNulls(activity, gameType) { a, type ->
                viewPager.adapter = ViewPagerInstrAdapter(
                    fragmentActivity = a,
                    gameType = type,
                    error = { error() }
                )
                TabLayoutMediator(tlInstructions, viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = getString(R.string.online_mode)
                        1 -> tab.text = getString(R.string.online_offline)
                    }
                }.attach()
            }
        }
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}