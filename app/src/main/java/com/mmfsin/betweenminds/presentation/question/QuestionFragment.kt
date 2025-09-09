package com.mmfsin.betweenminds.presentation.question

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentQuestionBinding
import com.mmfsin.betweenminds.domain.models.Phrase
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getNumberColor
import com.mmfsin.betweenminds.utils.handleAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
class QuestionFragment : BaseFragment<FragmentQuestionBinding, QuestionViewModel>() {

    override val viewModel: QuestionViewModel by viewModels()
    private lateinit var mContext: Context

    private var questions: List<Phrase> = emptyList()
    private var position = 0

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentQuestionBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPhrases()
    }

    override fun setUI() {
        binding.apply {
            loading.root.isVisible = true
            clQuestion.animateX(-1000f, 1)
            lottieCurtain.isVisible = false

            llBottomSlider.alpha = 0.4f
            bottomSlider.isEnabled = false

            llBtnHide.animateY(500f, 1)
            llBtnCheck.animateY(500f, 1)
            rematch.root.animateX(500f, 1)

            initialStates()
        }
    }

    private fun initialStates() {
        binding.apply {
            hideCurtain()
            llBottomSlider.handleAlpha(0.4f, 350) {
                bottomSlider.value = 0f
                bottomSliderValue(0f)
            }

            topSlider.isEnabled = true
            btnHide.isEnabled = true
            btnCheck.isEnabled = true
            rematch.btnRematch.isEnabled = true
        }
    }

    override fun setListeners() {
        binding.apply {
            topSlider.addOnChangeListener { _, value, _ -> topSliderValue(value) }
            bottomSlider.addOnChangeListener { _, value, _ -> bottomSliderValue(value) }

            btnHide.setOnClickListener {
                btnHide.isEnabled = false
                topSlider.isEnabled = false
                showCurtain()
                llBtnHide.animateY(500f, 500)

                countDown(350) {
                    llBottomSlider.showAlpha(500) { bottomSlider.isEnabled = true }
                    llBtnCheck.animateY(0f, 500)
                }
            }

            btnCheck.setOnClickListener {
                btnCheck.isEnabled = false
                bottomSlider.isEnabled = false
                hideCurtain()
                llBtnCheck.animateY(500f, 500)

                countDown(500) { rematch.root.animateX(0f, 500) }
            }

            rematch.btnRematch.setOnClickListener {
                rematch.btnRematch.isEnabled = false
                rematch.root.animateX(500f, 500)
                countDown(200) { initialStates() }
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is QuestionEvent.Phrases -> setPhrases(event.phrases)
                is QuestionEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setPhrases(phrases: List<Phrase>) {
        binding.apply {
            try {
                this@QuestionFragment.questions = phrases
                tvQuestion.text = questions[position].text
                countDown(1000) {
                    loading.root.isVisible = false
                }
                countDown(1200) {
                    clQuestion.animateX(0f, 500)
                    llBtnHide.animateY(0f, 500)
                }
            } catch (e: Exception) {
                error()
            }
        }
    }

    private fun showCurtain() {
        binding.apply {
            lottieCurtain.isVisible = true
            lottieCurtain.speed = 4f
            lottieCurtain.playAnimation()
        }
    }

    private fun hideCurtain() {
        binding.apply {
            lottieCurtain.animateY(-1000f, 500) {
                lottieCurtain.isVisible = false
                lottieCurtain.animateY(0f, 1)
            }
        }
    }

    private fun topSliderValue(value: Float) {
        binding.apply {
            tvTopNumber.text = "${value.toInt().absoluteValue}"
            tvTopNumber.setTextColor(getColor(mContext, getNumberColor(value.toInt())))
        }
    }

    private fun bottomSliderValue(value: Float) {
        binding.apply {
            tvBottomNumber.text = "${value.toInt().absoluteValue}"
            tvBottomNumber.setTextColor(getColor(mContext, getNumberColor(value.toInt())))

            val leftScale = 3f - ((value + 100f) / 200f) * 2f
            val rightScale = 1f + ((value + 100f) / 200f) * 2f

            ivRight.scaleX = leftScale
            ivRight.scaleY = leftScale

            ivLeft.scaleX = rightScale
            ivLeft.scaleY = rightScale

            if (value > 0) {
                ivLeft.setImageResource(R.drawable.ic_human_up)
                ivRight.setImageResource(R.drawable.ic_human_down)
            } else if (value == 0f) {
                ivLeft.setImageResource(R.drawable.ic_human_down)
                ivRight.setImageResource(R.drawable.ic_human_down)
            } else {
                ivLeft.setImageResource(R.drawable.ic_human_down)
                ivRight.setImageResource(R.drawable.ic_human_up)
            }
        }
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

