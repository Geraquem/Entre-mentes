package com.mmfsin.betweenminds.presentation.choose.online

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentHandleRoomBinding
import com.mmfsin.betweenminds.presentation.choose.interfaces.IHandleRoomListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinRoomFragment(private val listener: IHandleRoomListener) :
    BaseFragmentNoVM<FragmentHandleRoomBinding>() {

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
            btnContinue.button.setOnClickListener {
                val userName = tietUsername.text.toString()
                val roomId = tietRoomCode.text.toString().uppercase()
                if (roomId.isNotEmpty()) listener.joinRoom(userName, roomId)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}