package com.mmfsin.betweenminds.presentation.choose.online

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentHandleRoomBinding
import com.mmfsin.betweenminds.presentation.choose.interfaces.IHandleRoomListener
import com.mmfsin.betweenminds.utils.closeKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateRoomFragment(private val listener: IHandleRoomListener) :
    BaseFragmentNoVM<FragmentHandleRoomBinding>() {

    private lateinit var mContext: Context

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHandleRoomBinding.inflate(inflater, container, false)

    override fun setUI() {
        binding.apply {
            tvRoomCode.isVisible = false
            etRoomCode.isVisible = false
            btnContinue.button.text = getString(R.string.online_btn_create)
        }
    }

    override fun setListeners() {
        binding.apply {
            btnContinue.button.setOnClickListener {
                activity?.closeKeyboard()
                listener.createRoom()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}