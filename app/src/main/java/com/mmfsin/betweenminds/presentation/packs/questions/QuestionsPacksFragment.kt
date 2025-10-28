package com.mmfsin.betweenminds.presentation.packs.questions

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
import com.mmfsin.betweenminds.presentation.packs.manager.BillingManager
import com.mmfsin.betweenminds.presentation.packs.questions.adapter.IQuestionsPackListener
import com.mmfsin.betweenminds.presentation.packs.questions.adapter.QuestionsPackAdapter
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionsPacksFragment : BaseFragment<FragmentPacksBinding, QuestionsPacksViewModel>(),
    IQuestionsPackListener {

    override val viewModel: QuestionsPacksViewModel by viewModels()
    private lateinit var mContext: Context

    private var billingManager: BillingManager? = null

    private var questionsPackAdapter: QuestionsPackAdapter? = null

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
            loading.root.isVisible = true

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
                is QuestionsPacksEvent.SelectedPack -> {
                    questionsPackAdapter?.updateSelectedPack(event.selected)
                    binding.loading.root.isVisible = false
                }

                is QuestionsPacksEvent.QuestionsPacks -> setUpQuestionsPack(event.packs)
                is QuestionsPacksEvent.NewPackSelected -> {
                    questionsPackAdapter?.updateSelectedPack(event.packId)
                }

                is QuestionsPacksEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setUpQuestionsPack(packs: List<QuestionPack>) {
        binding.rvPacks.apply {
            layoutManager = LinearLayoutManager(activity)
            questionsPackAdapter = QuestionsPackAdapter(packs, this@QuestionsPacksFragment)
            adapter = questionsPackAdapter
        }
        viewModel.getSelectedQuestionPack()
    }

    override fun selectPack(packId: Int) = viewModel.selectQuestionPack(packId)

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}