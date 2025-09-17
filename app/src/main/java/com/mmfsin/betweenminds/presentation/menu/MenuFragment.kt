package com.mmfsin.betweenminds.presentation.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentMenuBinding
import com.mmfsin.betweenminds.presentation.MainActivity
import com.mmfsin.betweenminds.presentation.menu.MenuFragmentDirections.Companion.actionToChooseFragment
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MenuFragment : BaseFragment<FragmentMenuBinding, MenuViewModel>() {

    override val viewModel: MenuViewModel by viewModels()
    private lateinit var mContext: Context

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentMenuBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkVersion()
    }

    override fun setUI() {
        binding.apply {
            loading.root.isVisible = true

//            include.content.startRippleAnimation()

//            findNavController().navigate(actionToChooseFragment())
//            navigateTo(R.navigation.nav_graph_ranges)
//            navigateTo(R.navigation.nav_graph_question)
        }
    }

    override fun setListeners() {
        binding.apply {
            btnPlay.setOnClickListener { findNavController().navigate(actionToChooseFragment()) }
            btnHowToPlay.setOnClickListener {}
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is MenuEvent.VersionCompleted -> binding.loading.root.isVisible = false
                is MenuEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun navigateTo(navGraph: Int, strArgs: String? = null, booleanArgs: Boolean? = null) {
        (activity as MainActivity).openBedRockActivity(
            navGraph = navGraph, strArgs = strArgs, booleanArgs = booleanArgs
        )
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}