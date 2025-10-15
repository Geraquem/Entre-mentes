package com.mmfsin.betweenminds.presentation.instructions.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mmfsin.betweenminds.presentation.instructions.questions.InstrOQuestionFragment
import com.mmfsin.betweenminds.presentation.instructions.questions.InstrQuestionFragment
import com.mmfsin.betweenminds.presentation.instructions.ranges.InstrORangesFragment
import com.mmfsin.betweenminds.presentation.instructions.ranges.InstrRangesFragment
import com.mmfsin.betweenminds.utils.QUESTIONS_TYPE
import com.mmfsin.betweenminds.utils.RANGES_TYPE

class ViewPagerInstrAdapter(
    fragmentActivity: FragmentActivity,
    private val gameType: String,
    val error: () -> Unit
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        if (gameType != QUESTIONS_TYPE && gameType != RANGES_TYPE) error()

        val fragments = when (gameType) {
            /** Questions */
            QUESTIONS_TYPE -> Pair(InstrOQuestionFragment(), InstrQuestionFragment())

            /** Ranges */
            else -> Pair(InstrORangesFragment(), InstrRangesFragment())
        }

        return when (position) {
            0 -> fragments.first
            else -> fragments.second
        }
    }
}