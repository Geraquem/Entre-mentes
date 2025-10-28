package com.mmfsin.betweenminds.presentation.packs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayoutMediator
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentPacksVpBinding
import com.mmfsin.betweenminds.presentation.packs.adapter.ViewPagerPacksAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PacksVPagerFragment : BaseFragmentNoVM<FragmentPacksVpBinding>() {

    private lateinit var mContext: Context

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPacksVpBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is BedRockActivity) (activity as BedRockActivity).skipExitDialog = true
        setUpViewPager()
    }

    override fun setUI() {
        binding.toolbar.btnInstructions.isVisible = false
    }

    override fun setListeners() {
        binding.toolbar.btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
    }

    private fun setUpViewPager() {
        binding.apply {
            activity?.let { a ->
                viewPager.adapter = ViewPagerPacksAdapter(fragmentActivity = a)
                TabLayoutMediator(tlPacks, viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = getString(R.string.pack_questions)
                        1 -> tab.text = getString(R.string.pack_ranges)
                    }
                }.attach()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}