package com.mmfsin.betweenminds.presentation.instructions.questions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentInstrQuestionsOnlineBinding
import com.mmfsin.betweenminds.utils.moveHumans
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstrOQuestionFragment : BaseFragmentNoVM<FragmentInstrQuestionsOnlineBinding>() {

    private lateinit var mContext: Context

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentInstrQuestionsOnlineBinding.inflate(inflater, container, false)

    override fun setUI() {
        binding.apply {
            people1.apply {
                moveHumans(people1, 20)
                etPlayerBlue.isEnabled = false
                etPlayerOrange.isEnabled = false
                etPlayerBlue.setText(R.string.instr_questions_maria)
                etPlayerOrange.setText(R.string.instr_questions_juan)
                percentOneBlue.text = getString(R.string.instr_questions_percent_eighty)
                percentOneOrange.text = getString(R.string.instr_questions_percent_twenty)
            }

            people2.apply {
                moveHumans(people2, 38)
                etPlayerBlue.isEnabled = false
                etPlayerOrange.isEnabled = false
                etPlayerBlue.setText(R.string.instr_questions_maria)
                etPlayerOrange.setText(R.string.instr_questions_juan)

                percentOneBlue.text = getString(R.string.instr_questions_percent_eighty)
                percentOneOrange.text = getString(R.string.instr_questions_percent_twenty)
                percentTwoBlue.text = getString(R.string.instr_questions_percent_sixty_two)
                percentTwoOrange.text = getString(R.string.instr_questions_percent_thirty_eight)

                llPercentTwoBlue.alpha = 1f
                llPercentTwoOrange.alpha = 1f
                icQuestionTwoBlue.isVisible = false
                icQuestionTwoOrange.isVisible = false
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

