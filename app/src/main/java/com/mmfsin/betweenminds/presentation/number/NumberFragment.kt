package com.mmfsin.betweenminds.presentation.number

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragmentNoVM
import com.mmfsin.betweenminds.databinding.FragmentNumberBinding
import com.mmfsin.betweenminds.presentation.number.adapter.BarAdapter
import com.mmfsin.betweenminds.utils.animateX
import com.mmfsin.betweenminds.utils.animateY
import com.mmfsin.betweenminds.utils.countDown
import com.mmfsin.betweenminds.utils.getNumberColor
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
class NumberFragment : BaseFragmentNoVM<FragmentNumberBinding>() {

    private lateinit var mContext: Context

    private var numberToGuess = 0
    private var sliderNumber = 0

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentNumberBinding.inflate(inflater, container, false)


    override fun setUI() {
        binding.apply {
            buildCurtain()

            llBtnHide.animateY(500f, 10)
            llBtnCheck.animateY(500f, 10)
            rematch.root.animateX(500f, 10)

            startGame()
        }
    }

    private fun buildCurtain() {
        binding.rvCurtain.apply {
            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels

            val barWidth = (24 * resources.displayMetrics.density).toInt()
            val spaceWidth = (2 * resources.displayMetrics.density).toInt()
            val totalUnit = barWidth + spaceWidth

            val numberOfBars = screenWidth / totalUnit

            layoutManager = LinearLayoutManager(mContext, HORIZONTAL, false)
            adapter = BarAdapter(numberOfBars)

        }
    }

    private fun startGame() {
        binding.apply {
            hideCurtain()
            slotMachine()
            slider.value = 0f
            setSliderValue(0f)
            llSlider.hideAlpha(100)

            btnHide.isEnabled = true
            btnCheck.isEnabled = true
            slider.isEnabled = true
            rematch.btnRematch.isEnabled = true
        }
    }

    private fun slotMachine() {
        binding.apply {
            tvNumberToGuess.setTextColor(getColor(mContext, R.color.dark_grey))
            val finalNumber = (-100..100).random()
            numberToGuess = finalNumber
            val animator = ValueAnimator.ofInt(1, 100)
            animator.duration = 2500
            animator.addUpdateListener { _ ->
                val random = (0..100).random()
                tvNumberToGuess.text = "$random"
            }
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    tvNumberToGuess.text = "${finalNumber.absoluteValue}"
                    tvNumberToGuess.setTextColor(getColor(mContext, getNumberColor(finalNumber)))

                    countDown(500) {
                        llBtnHide.animateY(0f, 500)
                    }
                }
            })
            animator.start()
        }
    }

    override fun setListeners() {
        binding.apply {
            slider.addOnChangeListener { _, value, _ -> setSliderValue(value) }

            btnHide.setOnClickListener {
                btnHide.isEnabled = false
                showCurtain()
                llBtnHide.animateY(500f, 500)

                countDown(350) {
                    llSlider.showAlpha(500)
                    llBtnCheck.animateY(0f, 500)
                }
            }

            btnCheck.setOnClickListener {
                btnCheck.isEnabled = false
                slider.isEnabled = false
                hideCurtain()
                llBtnCheck.animateY(500f, 500)

                countDown(500) { rematch.root.animateX(0f, 500) }
            }

            rematch.btnRematch.setOnClickListener {
                rematch.btnRematch.isEnabled = false
                rematch.root.animateX(500f, 500)
                countDown(200) { startGame() }
            }
        }
    }

    private fun showCurtain() {
        binding.apply {
            rvCurtain.post {
                rvCurtain.animate()
                    .translationX(0f)
                    .setDuration(1000)
                    .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator())
                    .start()
            }
        }
    }

    private fun hideCurtain() {
        binding.apply {
            rvCurtain.post {
                val curtainWidth = rvCurtain.width.toFloat()
                rvCurtain.animate()
                    .translationX(-curtainWidth)
                    .setDuration(1000)
                    .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator())
                    .start()
            }
        }
    }

    private fun setSliderValue(value: Float) {
        binding.apply {
            sliderNumber = value.toInt()
            tvSliderNumber.text = "${value.toInt().absoluteValue}"
            tvSliderNumber.setTextColor(getColor(mContext, getNumberColor(value.toInt())))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}