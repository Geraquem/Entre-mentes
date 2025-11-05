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
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentPackDetailBinding
import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.QuestionsPack
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.RangesPack
import com.mmfsin.betweenminds.presentation.packs.detail.adapter.QDetailPackAdapter
import com.mmfsin.betweenminds.presentation.packs.detail.adapter.RDetailPackAdapter
import com.mmfsin.betweenminds.presentation.packs.manager.BillingManager
import com.mmfsin.betweenminds.presentation.packs.manager.IBillingListener
import com.mmfsin.betweenminds.presentation.packs.manager.SelectedManager
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailPackFragment : BaseFragment<FragmentPackDetailBinding, DetailPackViewModel>(),
    IBillingListener {

    override val viewModel: DetailPackViewModel by viewModels()
    private lateinit var mContext: Context

    private var qPack: QuestionsPack? = null
    private var rPack: RangesPack? = null

    private var billingManager: BillingManager? = null

    @Inject
    lateinit var selectedManager: SelectedManager

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
            viewModel.getQuestions(p.packNumber)
        }

        rPack?.let { p ->
            setUpRangePack(p)
            viewModel.getRanges(p.packNumber)
        }
    }

    override fun setUI() {
        binding.apply {
            loading.root.isVisible = false
            toolbar.btnInstructions.isVisible = false
            tvLoading.isVisible = true
            btnPurchase.button.text = getString(R.string.pack_purchase_btn)
            btnSelect.button.text = getString(R.string.pack_selected_btn)
        }
    }

    override fun setListeners() {
        binding.apply {
            toolbar.btnBack.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }

            btnPurchase.button.setOnClickListener { purchasePack() }

            btnSelect.button.setOnClickListener {
                qPack?.let { viewModel.selectQuestionPack(it.packNumber) }
                rPack?.let { viewModel.selectRangesPack(it.packNumber) }
            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is DetailPackEvent.QuestionsPack -> setUpQuestionsExample(event.data)
                is DetailPackEvent.RangesPack -> setUpRangesExample(event.data)
                is DetailPackEvent.Selected -> selectPack()
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

            handleIfPurchase(pack.purchased, pack.selected)
        }
    }

    private fun setUpRangePack(pack: RangesPack) {
        binding.apply {
            Glide.with(mContext).load(pack.packIcon).into(ivPackIcon)
            tvPrice.text = pack.packPrice
            tvTitle.text = pack.packTitle
            tvDescription.text = pack.packDescription

            handleIfPurchase(pack.purchased, pack.selected)
        }
    }

    private fun handleIfPurchase(purchased: Boolean, selected: Boolean) {
        binding.apply {
            tvPrice.isVisible = !purchased
            btnPurchase.root.isVisible = !purchased

            if (!purchased) {
                btnSelect.root.isVisible = false
                tvSelected.isVisible = false
            } else {
                btnSelect.root.isVisible = !selected
                tvSelected.isVisible = selected
            }

            loading.root.isVisible = false
        }
    }

    private fun setUpQuestionsExample(questions: List<Question>) {
        binding.apply {
            rvExamples.apply {
                layoutManager = LinearLayoutManager(mContext)
                adapter = QDetailPackAdapter(questions.map { it.question })
            }
            tvLoading.isVisible = false
        }
    }

    private fun setUpRangesExample(ranges: List<Range>) {
        binding.apply {
            rvExamples.apply {
                layoutManager = LinearLayoutManager(mContext)
                adapter = RDetailPackAdapter(ranges)
            }
            tvLoading.isVisible = false
        }
    }

    private fun selectPack() {
        var packNumber: Int? = null
        qPack?.let { packNumber = it.packNumber }
        rPack?.let { packNumber = it.packNumber }

        packNumber?.let { pN -> selectedManager.updateSelectedQuestionPackNumber(pN) }
        handleIfPurchase(purchased = true, selected = true)
    }

    private fun purchasePack() {
        var packId: String? = null
        qPack?.let { packId = it.packId }
        rPack?.let { packId = it.packId }

        activity?.let { a ->
            billingManager = BillingManager(a, this@DetailPackFragment)
            billingManager?.startConnection {
                packId?.let { id -> billingManager?.launchPurchase(a, id) } ?: run { error() }
            } ?: run {
                println("Parece que billingManager es nulo y no se puede comprar")
            }
        }
    }

    override fun purchasedCompleted(packId: String) {
        requireActivity().runOnUiThread {
            binding.loading.root.isVisible = true
            qPack?.let { viewModel.selectQuestionPack(it.packNumber) }
            rPack?.let { viewModel.selectRangesPack(it.packNumber) }
        }
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}