package com.mmfsin.betweenminds.presentation.packs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentPacksVpBinding
import com.mmfsin.betweenminds.presentation.packs.adapter.ViewPagerPacksAdapter
import com.mmfsin.betweenminds.utils.BEDROCK_BOOLEAN_ARGS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PacksVPFragment : BaseFragment<FragmentPacksVpBinding, PacksVPViewModel>() {

    override val viewModel: PacksVPViewModel by viewModels()

    private lateinit var mContext: Context

    private var openRanges: Boolean = false

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPacksVpBinding.inflate(inflater, container, false)

    override fun getBundleArgs() {
        activity?.intent?.let {
            openRanges = it.getBooleanExtra(BEDROCK_BOOLEAN_ARGS, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is BedRockActivity) (activity as BedRockActivity).skipExitDialog = true
        viewModel.checkIfPacksAreFree()
    }

    override fun setUI() {
        binding.toolbar.btnInstructions.isVisible = false
    }

    override fun setListeners() {
        binding.toolbar.btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is PacksVPEvent.FreePack -> setUpViewPager(event.areFree)
            }
        }
    }

    private fun setUpViewPager(areFree: Boolean) {
        binding.apply {
            activity?.let { a ->
                viewPager.adapter = ViewPagerPacksAdapter(fragmentActivity = a, areFree)
                TabLayoutMediator(tlPacks, viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = getString(R.string.pack_questions)
                        1 -> tab.text = getString(R.string.pack_ranges)
                    }
                }.attach()
            }

            if (openRanges) {
                viewPager.currentItem = 1
                tlPacks.getTabAt(1)?.select()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}