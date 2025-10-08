package com.mmfsin.betweenminds.presentation.choose.roomcode

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentRoomCodeBinding
import com.mmfsin.betweenminds.presentation.MainActivity
import com.mmfsin.betweenminds.utils.ROOM_ID
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomCodeFragment : BaseFragment<FragmentRoomCodeBinding, RoomCodeViewModel>() {

    override val viewModel: RoomCodeViewModel by viewModels()
    private lateinit var mContext: Context

    private var roomId: String? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRoomCodeBinding.inflate(inflater, container, false)

    override fun getBundleArgs() {
        roomId = arguments?.getString(ROOM_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomId?.let { id -> viewModel.waitForOtherPlayer(id) } ?: run { error() }
    }

    override fun setUI() {
        binding.apply {
            toolbar.btnInstructions.isVisible = false
            roomId?.let { id -> tvRoomId.text = id } ?: run { error() }
        }
    }

    override fun setListeners() {
        binding.apply {
            toolbar.btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is RoomCodeEvent.ListeningOtherPlayer -> {
                    roomId?.let { id ->
                        (activity as MainActivity).openBedRockActivity(
                            navGraph = R.navigation.nav_graph_online_ranges_creator,
                            strArgs = id,
                            booleanArgs = true
                        )
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                }

                is RoomCodeEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}