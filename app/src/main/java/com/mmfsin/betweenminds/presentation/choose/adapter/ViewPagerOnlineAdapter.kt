package com.mmfsin.betweenminds.presentation.choose.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mmfsin.betweenminds.presentation.choose.interfaces.IHandleRoomListener
import com.mmfsin.betweenminds.presentation.choose.online.CreateRoomFragment
import com.mmfsin.betweenminds.presentation.choose.online.JoinRoomFragment

class ViewPagerOnlineAdapter(
    fragmentActivity: FragmentActivity,
    private val listener: IHandleRoomListener
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> JoinRoomFragment(listener)
            else -> CreateRoomFragment(listener)
        }
    }
}