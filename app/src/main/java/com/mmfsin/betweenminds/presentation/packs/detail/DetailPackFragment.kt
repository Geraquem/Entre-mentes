package com.mmfsin.betweenminds.presentation.packs.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentPackDetailBinding
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.QuestionsPack
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.RangesPack
import com.mmfsin.betweenminds.presentation.packs.manager.BillingManager
import com.mmfsin.betweenminds.presentation.packs.questions.adapter.QExamplesPackAdapter
import com.mmfsin.betweenminds.presentation.packs.ranges.adapter.RExamplesPackAdapter
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailPackFragment : BaseFragment<FragmentPackDetailBinding, DetailPackViewModel>() {

    override val viewModel: DetailPackViewModel by viewModels()
    private lateinit var mContext: Context

    private var qPack: QuestionsPack? = null
    private var rPack: RangesPack? = null

    private var billingManager: BillingManager? = null

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPackDetailBinding.inflate(inflater, container, false)

    override fun getBundleArgs() {
        val args: DetailPackFragmentArgs by navArgs()
        qPack = args.questionPack
        rPack = args.rangePack
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qPack?.let { p ->
            setUpQuestionPack(p)
            viewModel.getQuestions(p.packId)
        }

        rPack?.let { p ->
            setUpRangePack(p)
            viewModel.getRanges(p.packId)
        }
    }

    override fun setUI() {
        binding.apply {
            toolbar.btnInstructions.isVisible = false
        }
    }

    override fun setListeners() {
        binding.apply {
            toolbar.btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is DetailPackEvent.QuestionsPack -> setUpQuestionsExample(event.data)
                is DetailPackEvent.RangesPack -> setUpRangesExample(event.data)
                is DetailPackEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setUpQuestionPack(pack: QuestionsPack) {
        binding.apply {
            Glide.with(mContext).load(pack.packIcon).into(ivPackIcon)
            tvPrice.text = pack.packPrice
            tvTitle.text = pack.packTitle
            tvDescription.text = pack.packDescription
        }
    }

    private fun setUpRangePack(pack: RangesPack) {
        binding.apply {
            Glide.with(mContext).load(pack.packIcon).into(ivPackIcon)
            tvPrice.text = pack.packPrice
            tvTitle.text = pack.packTitle
            tvDescription.text = pack.packDescription
        }
    }

    private fun setUpQuestionsExample(questions: List<Question>) {
        binding.rvExamples.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = QExamplesPackAdapter(questions.map { it.question })
        }
    }

    private fun setUpRangesExample(ranges: List<Range>) {
        binding.rvExamples.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = RExamplesPackAdapter(ranges)
        }
    }

    private fun error() = activity?.showErrorDialog(goBack = true)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}