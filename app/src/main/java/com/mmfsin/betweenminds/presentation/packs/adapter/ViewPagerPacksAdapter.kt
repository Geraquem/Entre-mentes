package com.mmfsin.betweenminds.presentation.packs.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mmfsin.betweenminds.presentation.packs.questions.QuestionsPacksFragment
import com.mmfsin.betweenminds.presentation.packs.ranges.RangesPacksFragment

class ViewPagerPacksAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> QuestionsPacksFragment()
            else -> RangesPacksFragment()
        }
    }
}