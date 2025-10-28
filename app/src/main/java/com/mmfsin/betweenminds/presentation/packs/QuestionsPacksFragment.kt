package com.mmfsin.betweenminds.presentation.packs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentPacksBinding
import com.mmfsin.betweenminds.domain.models.QuestionPack
import com.mmfsin.betweenminds.presentation.packs.adapter.QuestionsPackAdapter
import com.mmfsin.betweenminds.presentation.packs.manager.BillingManager
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionsPacksFragment : BaseFragment<FragmentPacksBinding, PacksViewModel>() {

    override val viewModel: PacksViewModel by viewModels()
    private lateinit var mContext: Context

    private var billingManager: BillingManager? = null

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPacksBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { billingManager = BillingManager(it) }
        viewModel.getQuestionsPack()
    }

    override fun setUI() {
        binding.apply {
            loading.root.isVisible = false

        }
    }

    override fun setListeners() {
        binding.apply {
//            btnPurchase.button.setOnClickListener {
//                billingManager?.startConnection {
//                    activity?.let {
//                        billingManager?.launchPurchase(it, "ranges_pack_1")
//                    }
//                }
//            }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is PacksEvent.QuestionPacks -> setUpQuestionsPack(event.packs)
                is PacksEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setUpQuestionsPack(packs: List<QuestionPack>) {
        binding.rvPacks.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = QuestionsPackAdapter(packs)
        }
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}