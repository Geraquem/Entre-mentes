package com.mmfsin.betweenminds.presentation.choose

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentChooseBinding
import com.mmfsin.betweenminds.presentation.MainActivity
import com.mmfsin.betweenminds.presentation.choose.ChooseFragmentDirections.Companion.actionToRoomCodeFragment
import com.mmfsin.betweenminds.presentation.choose.adapter.ViewPagerOnlineAdapter
import com.mmfsin.betweenminds.presentation.choose.dialogs.NotAbleToJoinDialog
import com.mmfsin.betweenminds.presentation.choose.interfaces.IHandleRoomListener
import com.mmfsin.betweenminds.utils.GAME_TYPE
import com.mmfsin.betweenminds.utils.QUESTIONS_TYPE
import com.mmfsin.betweenminds.utils.RANGES_TYPE
import com.mmfsin.betweenminds.utils.checkNotNulls
import com.mmfsin.betweenminds.utils.showErrorDialog
import com.mmfsin.betweenminds.utils.showFragmentDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseFragment : BaseFragment<FragmentChooseBinding, ChooseViewModel>(), IHandleRoomListener {

    override val viewModel: ChooseViewModel by viewModels()
    private lateinit var mContext: Context

    private var gameType: String? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChooseBinding.inflate(inflater, container, false)

    override fun getBundleArgs() {
        arguments?.let { gameType = it.getString(GAME_TYPE) } ?: run { error(goBack = true) }
    }

    override fun onResume() {
        super.onResume()
        gameType?.let { type -> viewModel.getSelectedPack(type) }
    }

    override fun setUI() {
        setUpViewPager()
        binding.apply {
            loading.root.isVisible = false
            toolbar.btnInstructions.isVisible = false

            when (gameType) {
                QUESTIONS_TYPE -> clRanges.isVisible = false
                RANGES_TYPE -> clQuestions.isVisible = false
                else -> {}
            }

            btnOffline.button.text = getString(R.string.online_btn_start)
        }
    }

    override fun setListeners() {
        binding.apply {
            toolbar.btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }

            tvChangePack.setOnClickListener {
                (activity as MainActivity).openBedRockActivity(R.navigation.nav_graph_packs)
            }

            btnOffline.button.setOnClickListener {
                val navGraph = when (gameType) {
                    QUESTIONS_TYPE -> R.navigation.nav_graph_questions
                    RANGES_TYPE -> R.navigation.nav_graph_ranges
                    else -> null
                }
                navGraph?.let { ng -> navigateTo(navGraph = ng) } ?: run { error(goBack = false) }
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is ChooseEvent.SelectedPack -> setPack(event.data)

                is ChooseEvent.RoomCreated -> {
                    println("Room created with code: ${event.roomId}")
                    binding.loading.root.isVisible = false
                    checkNotNulls(event.roomId, gameType) { id, type ->
                        findNavController().navigate(actionToRoomCodeFragment(id, type))
                        event.roomId = null
                    }
                }

                is ChooseEvent.JoinedToRoom -> {
                    binding.loading.root.isVisible = false
                    if (event.joined) {
                        val navGraph = when (gameType) {
                            QUESTIONS_TYPE -> R.navigation.nav_graph_online_questions_joined
                            RANGES_TYPE -> R.navigation.nav_graph_online_ranges
                            else -> null
                        }
                        navGraph?.let { ng ->
                            navigateTo(
                                navGraph = ng, strArgs = event.roomId, booleanArgs = false
                            )
                        } ?: run { error(goBack = false) }

                    } else activity?.showFragmentDialog(NotAbleToJoinDialog())
                }

                is ChooseEvent.SomethingWentWrong -> {
                    binding.loading.root.isVisible = false
                    error(goBack = false)
                }
            }
        }
    }

    private fun setPack(data: Pair<String?, String?>) {
        binding.apply {
            data.first?.let { icon -> Glide.with(mContext).load(icon).into(binding.ivPackIcon) }
            data.second?.let { title -> tvPackName.text = title }
        }
    }

    private fun setUpViewPager() {
        binding.apply {
            activity?.let {
                viewPager.adapter =
                    ViewPagerOnlineAdapter(fragmentActivity = it, this@ChooseFragment)
                TabLayoutMediator(tlRoom, viewPager) { tab, position ->
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

    override fun createRoom() {
        binding.apply {
            loading.root.isVisible = true
            gameType?.let { type -> viewModel.createRoom(type) } ?: run { error(false) }
        }
    }

    override fun joinRoom(roomId: String) {
        binding.apply {
            loading.root.isVisible = true
            gameType?.let { type -> viewModel.joinRoom(roomId, type) } ?: run { error(false) }
        }
    }

    private fun navigateTo(navGraph: Int, strArgs: String? = null, booleanArgs: Boolean? = null) {
        (activity as MainActivity).openBedRockActivity(
            navGraph = navGraph, strArgs = strArgs, booleanArgs = booleanArgs
        )
    }

    private fun error(goBack: Boolean) = activity?.showErrorDialog(goBack)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}