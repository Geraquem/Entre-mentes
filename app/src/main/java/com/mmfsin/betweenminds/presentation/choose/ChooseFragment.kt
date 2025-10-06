package com.mmfsin.betweenminds.presentation.choose

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentChooseBinding
import com.mmfsin.betweenminds.presentation.MainActivity
import com.mmfsin.betweenminds.presentation.choose.adapter.ViewPagerOnlineAdapter
import com.mmfsin.betweenminds.presentation.choose.interfaces.IHandleRoomListener
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseFragment : BaseFragment<FragmentChooseBinding, ChooseViewModel>(), IHandleRoomListener {

    override val viewModel: ChooseViewModel by viewModels()
    private lateinit var mContext: Context

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChooseBinding.inflate(inflater, container, false)

    override fun setUI() {
        setUpViewPager()
        binding.apply {
            loading.root.isVisible = false
            toolbar.btnInstructions.isVisible = false
            btnOffline.button.text = getString(R.string.online_btn_start)
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
                is ChooseEvent.RoomCreated -> {
                    Toast.makeText(mContext, event.roomId, Toast.LENGTH_SHORT).show()
                }

                is ChooseEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setUpViewPager() {
        binding.apply {
            activity?.let {
                viewPager.adapter =
                    ViewPagerOnlineAdapter(fragmentActivity = it, this@ChooseFragment)
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = getString(R.string.online_join_room)
                        1 -> tab.text = getString(R.string.online_create_room)
                    }
                }.attach()
                viewPager.disableSwipe()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun ViewPager2.disableSwipe() {
        (getChildAt(0) as RecyclerView).apply {
            setOnTouchListener { _, _ -> true }
        }
    }

    override fun joinRoom(roomCode: String, userName: String) {

    }

    override fun createRoom(userName: String) {
        binding.apply {
            loading.root.isVisible = true
            viewModel.createRoom(userName)
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