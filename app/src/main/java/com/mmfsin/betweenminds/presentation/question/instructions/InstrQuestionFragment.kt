package com.mmfsin.betweenminds.presentation.question.instructions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color.WHITE
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentInstrQuestionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstrQuestionFragment : BaseFragmentNoVM<FragmentInstrQuestionsBinding>() {

    private lateinit var mContext: Context

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentInstrQuestionsBinding.inflate(inflater, container, false)

    override fun setUI() {
        binding.apply {
//            (activity as BedRockActivity).setUpToolbar(
//                instructionsVisible = false
//            )

            exampleSlider.apply {
                bgSlider.value = 80f

                slider.apply {
                    isEnabled = false
                    thumbTintList = ColorStateList.valueOf(WHITE)
                    value = 80f
                }

                tvPercentLeft.text = getPercents().first
                tvPercentRight.text = getPercents().second
            }
        }
    }

    private fun getPercents(): Pair<String, String> {
        val value = 80
        return Pair("$value", "${(100 - value)}")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

