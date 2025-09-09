package com.mmfsin.betweenminds.presentation.number

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.viewModels
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentNumberBinding
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
class NumberFragment : BaseFragment<FragmentNumberBinding, NumberViewModel>() {

    override val viewModel: NumberViewModel by viewModels()
    private lateinit var mContext: Context

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentNumberBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setUI() {
        binding.apply {
            slider.addOnChangeListener { _, value, _ ->
                setSliderValue(value)
            }
        }
    }

    override fun setListeners() {
        binding.apply {
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is NumberEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setSliderValue(value: Float) {
        binding.apply {
            valueText.text = "${value.toInt().absoluteValue}"
            val color = if (value > 0) R.color.blue else if (value == 0f) R.color.black
            else R.color.orange
            valueText.setTextColor(getColor(mContext, color))
        }
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}