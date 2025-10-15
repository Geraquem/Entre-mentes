package com.mmfsin.betweenminds.presentation.instructions.questions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentInstrQuestionsBinding
import com.mmfsin.betweenminds.utils.handlePercentsPlayerOne
import com.mmfsin.betweenminds.utils.handlePercentsPlayerTwo
import com.mmfsin.betweenminds.utils.moveHumans
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstrQuestionFragment : BaseFragmentNoVM<FragmentInstrQuestionsBinding>() {

    private lateinit var mContext: Context

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentInstrQuestionsBinding.inflate(inflater, container, false)

    override fun setUI() {
        binding.apply {
            people1.apply {
                etPlayerBlue.isEnabled = false
                etPlayerOrange.isEnabled = false
                etPlayerBlue.setText(R.string.instr_questions_maria)
                etPlayerOrange.setText(R.string.instr_questions_juan)
                opinionOne.isVisible = false
                opinionTwo.isVisible = false
            }

            people2.apply {
                moveHumans(people2,20)
                etPlayerBlue.isEnabled = false
                etPlayerOrange.isEnabled = false
                etPlayerBlue.setText(R.string.instr_questions_maria)
                etPlayerOrange.setText(R.string.instr_questions_juan)
                percentOneBlue.text = getString(R.string.instr_questions_percent_eighty)
                percentOneOrange.text = getString(R.string.instr_questions_percent_twenty)
            }

            people3.apply {
                moveHumans(people3,38)
                handlePercentsPlayerOne(people3, show = false)
                handlePercentsPlayerTwo(people3, show = true)
                etPlayerBlue.isEnabled = false
                etPlayerOrange.isEnabled = false
                etPlayerBlue.setText(R.string.instr_questions_maria)
                etPlayerOrange.setText(R.string.instr_questions_juan)
                percentTwoBlue.text = getString(R.string.instr_questions_percent_sixty_two)
                percentTwoOrange.text = getString(R.string.instr_questions_percent_thirty_eight)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

