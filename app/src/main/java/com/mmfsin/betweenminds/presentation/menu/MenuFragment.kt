package com.mmfsin.betweenminds.presentation.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentMenuBinding
import com.mmfsin.betweenminds.presentation.MainActivity
import com.mmfsin.betweenminds.presentation.menu.dialogs.SelectorSheet
import com.mmfsin.betweenminds.presentation.menu.interfaces.ISelectorListener
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : BaseFragment<FragmentMenuBinding, MenuViewModel>(), ISelectorListener {

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
        }
    }

    override fun setListeners() {
        binding.apply {
            tvAppName.setOnClickListener { openSelector() }
            icPlay.setOnClickListener { openSelector() }
        }
    }

    private fun openSelector() {
        rotateImage {
            activity?.showFragmentDialog(SelectorSheet(this@MenuFragment))
        }
    }

    private fun rotateImage(onEnd: (() -> Unit)? = null) {
        val image = binding.icPlay
        image.animate()
            .rotation(-90f)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(150)
            .withEndAction {
                onEnd?.invoke()
                countDown(300) { image.rotation = 0f }
            }
            .start()
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
                        tvAppName.showAlpha(1500) { icPlay.showAlpha(1000) }
                    }
                } else {
                    tvAppName.showAlpha(10)
                    icPlay.showAlpha(10)
                }
            }
        }
    }

    override fun openQuestionsMode() = navigateTo(R.navigation.nav_graph_questions)
    override fun openQuestionsInstructions() = navigateTo(R.navigation.nav_graph_instr_questions)

    override fun openRangesMode() = navigateTo(R.navigation.nav_graph_ranges)
    override fun openRangesInstructions() = navigateTo(R.navigation.nav_graph_instr_ranges)

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