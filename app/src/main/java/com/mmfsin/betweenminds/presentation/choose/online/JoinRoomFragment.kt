package com.mmfsin.betweenminds.presentation.choose.online

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentHandleRoomBinding
import com.mmfsin.betweenminds.presentation.MainActivity
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinRoomFragment : BaseFragmentNoVM<FragmentHandleRoomBinding>() {

    private lateinit var mContext: Context

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHandleRoomBinding.inflate(inflater, container, false)

    override fun setUI() {
        binding.apply {
            btnContinue.button.text = getString(R.string.online_btn_join)
        }
    }

    override fun setListeners() {
        binding.apply {
        }
    }

//    override fun observe() {
//        viewModel.event.observe(this) { event ->
//            when (event) {
//                is ChooseEvent.SomethingWentWrong -> error()
//            }
//        }
//    }

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