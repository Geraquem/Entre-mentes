package com.mmfsin.betweenminds.presentation.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentMenuBinding
import com.mmfsin.betweenminds.presentation.MainActivity
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : BaseFragment<FragmentMenuBinding, MenuViewModel>() {

    override val viewModel: MenuViewModel by viewModels()
    private lateinit var mContext: Context

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentMenuBinding.inflate(inflater, container, false)

    override fun setUI() {
        binding.apply {
            navigateTo(R.navigation.nav_graph_number)
//            navigateTo(R.navigation.nav_graph_question)

            btnModeNumber.setOnClickListener { navigateTo(R.navigation.nav_graph_number) }
            btnModeQuestion.setOnClickListener { navigateTo(R.navigation.nav_graph_question) }
        }
    }

    override fun setListeners() {
        binding.apply {}
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is MenuEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun navigateTo(navGraph: Int, strArgs: String? = null, booleanArgs: Boolean? = null) {
        (activity as MainActivity).openBedRockActivity(
            navGraph = navGraph,
            strArgs = strArgs,
            booleanArgs = booleanArgs
        )
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}