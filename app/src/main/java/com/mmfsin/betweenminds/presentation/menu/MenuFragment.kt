package com.mmfsin.betweenminds.presentation.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentMenuBinding
import com.mmfsin.betweenminds.presentation.MainActivity
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
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

            llTitle.hideAlpha(1)
            llButtons.animateY(1000f, 1)

//            findNavController().navigate(actionToChooseFragment())
//            navigateTo(R.navigation.nav_graph_ranges)
//            navigateTo(R.navigation.nav_graph_questions)
        }
    }

    override fun setListeners() {
        binding.apply {
//            btnPlay.setOnClickListener { findNavController().navigate(actionToChooseFragment()) }
            btnPlay.setOnClickListener { navigateTo(R.navigation.nav_graph_ranges) }
            btnHowToPlay.setOnClickListener {}
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is MenuEvent.VersionCompleted -> showAnimations()
                is MenuEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun showAnimations() {
        binding.apply {
            loading.root.isVisible = false
            if (activity is MainActivity) {
                if ((activity as MainActivity).firstInit) {
                    (activity as MainActivity).firstInit = false
                    countDown(500) {
                        llTitle.showAlpha(2000)
                        llButtons.animateY(0f, 1000)
                    }
                } else {
                    llTitle.showAlpha(10)
                    llButtons.animateY(0f, 10)
                }
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