package com.mmfsin.betweenminds.presentation.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentMenuBinding
import com.mmfsin.betweenminds.presentation.MainActivity
import com.mmfsin.betweenminds.presentation.menu.MenuFragmentDirections.Companion.actionToChooseFragment
import com.mmfsin.betweenminds.presentation.menu.dialogs.SelectorSheet
import com.mmfsin.betweenminds.presentation.menu.interfaces.ISelectorListener
import com.mmfsin.betweenminds.utils.QUESTIONS_TYPE
import com.mmfsin.betweenminds.utils.RANGES_TYPE
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : BaseFragment<FragmentMenuBinding, MenuViewModel>(), ISelectorListener {

    override val viewModel: MenuViewModel by viewModels()
    private lateinit var mContext: Context

    private var codeCounter = 0

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

            llButtons.isVisible = false
            llButtons.animateY(500f, 10) { btnPlay.root.isVisible = true }
            btnPlay.button.text = getString(R.string.selector_play)
            btnPacks.button.text = getString(R.string.menu_packs)

            /*********************************************************************************/
//            findNavController().navigate(actionToChooseFragment(QUESTIONS_TYPE))
//            findNavController().navigate(actionToChooseFragment(RANGES_TYPE))
            /*********************************************************************************/
        }
    }

    override fun setListeners() {
        binding.apply {
            tvAppName.setOnClickListener {
                codeCounter++
                if (codeCounter == 20) viewModel.setFreePacks()
            }

            btnPlay.root.setOnClickListener { openSelector() }
            btnPacks.button.setOnClickListener {
                navigateTo(navGraph = R.navigation.nav_graph_packs)
            }
        }
    }

    private fun openSelector() {
        activity?.showFragmentDialog(SelectorSheet(this@MenuFragment))
    }

    private fun rotateImage(onEnd: (() -> Unit)? = null) {
        val image = binding.icPlay
        image.animate().rotation(-90f).setInterpolator(DecelerateInterpolator()).setDuration(150)
            .withEndAction {
                onEnd?.invoke()
                countDown(200) { image.rotation = 0f }
            }.start()
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is MenuEvent.VersionCompleted -> showAnimations()
                is MenuEvent.FreePacks -> freePacks()
                is MenuEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun showAnimations() {
        binding.apply {
            loading.root.isVisible = false
            llButtons.isVisible = true
            if (activity is MainActivity) {
                if ((activity as MainActivity).firstInit) {
                    (activity as MainActivity).firstInit = false
                    countDown(1000) {
                        tvAppName.showAlpha(1000) {
                            llButtons.animateY(0f, 500)
                        }
                    }
                } else {
                    tvAppName.showAlpha(10)
                    llButtons.animateY(0f, 10)
                }
            }
        }
    }

    override fun openQuestionsMode() =
        findNavController().navigate(actionToChooseFragment(QUESTIONS_TYPE))

    override fun openQuestionsInstructions() = navigateTo(
        navGraph = R.navigation.nav_graph_instructions, strArgs = QUESTIONS_TYPE
    )

    override fun openRangesMode() =
        findNavController().navigate(actionToChooseFragment(RANGES_TYPE))

    override fun openRangesInstructions() = navigateTo(
        navGraph = R.navigation.nav_graph_instructions, strArgs = RANGES_TYPE
    )

    private fun navigateTo(navGraph: Int, strArgs: String? = null, booleanArgs: Boolean? = null) {
        (activity as MainActivity).openBedRockActivity(
            navGraph = navGraph, strArgs = strArgs, booleanArgs = booleanArgs
        )
    }

    private fun freePacks() =
        Toast.makeText(mContext, R.string.pack_free_set, Toast.LENGTH_SHORT).show()

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}