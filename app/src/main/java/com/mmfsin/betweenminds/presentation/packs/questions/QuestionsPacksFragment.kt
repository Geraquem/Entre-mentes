package com.mmfsin.betweenminds.presentation.packs.questions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.QueryProductDetailsParams
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.databinding.FragmentPacksBinding
import com.mmfsin.betweenminds.domain.models.QuestionsPack
import com.mmfsin.betweenminds.presentation.packs.PacksVPagerFragmentDirections.Companion.actionToPackDetail
import com.mmfsin.betweenminds.presentation.packs.manager.BillingManager
import com.mmfsin.betweenminds.presentation.packs.manager.IBillingListener
import com.mmfsin.betweenminds.presentation.packs.questions.adapter.IQuestionsPackListener
import com.mmfsin.betweenminds.presentation.packs.questions.adapter.QuestionsPackAdapter
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionsPacksFragment : BaseFragment<FragmentPacksBinding, QuestionsPacksViewModel>(),
    IQuestionsPackListener, IBillingListener {

    override val viewModel: QuestionsPacksViewModel by viewModels()
    private lateinit var mContext: Context

    private var billingManager: BillingManager? = null

    private var questionsPackAdapter: QuestionsPackAdapter? = null

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPacksBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getQuestionsPack()
    }

    override fun onResume() {
        super.onResume()
        val a = 2
    }

    override fun setUI() {
        binding.loading.root.isVisible = true
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is QuestionsPacksEvent.SelectedPack -> {
                    questionsPackAdapter?.updateSelectedPack(event.selected)
                    binding.loading.root.isVisible = false
                }

                is QuestionsPacksEvent.QuestionsPacks -> checkPurchasedPacks(event.packs)
                is QuestionsPacksEvent.NewPackSelected -> {
                    questionsPackAdapter?.updateSelectedPack(event.packNumber)
                }

                is QuestionsPacksEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun checkPurchasedPacks(packs: List<QuestionsPack>) {
        activity?.let { a ->
            billingManager = BillingManager(a, this@QuestionsPacksFragment)
            billingManager?.startConnection {
                billingManager?.queryPurchasedIds(
                    onResult = { ownedPackages ->

                        val test = listOf("pack_questions_love")

                        val productIds = packs.let { p ->
                            p.filter { it.packNumber != 0 }
                                .map { it.packId }
                        }

                        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                            .setProductList(
                                productIds.map { id ->
                                    QueryProductDetailsParams.Product.newBuilder()
                                        .setProductId(id)
                                        .setProductType(BillingClient.ProductType.INAPP)
                                        .build()
                                }
                            ).build()

                        billingManager?.billingClient?.queryProductDetailsAsync(
                            queryProductDetailsParams
                        ) { billingResult, productDetailsList ->
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                //Mapa: productId -> Precio
                                val pricesMap = productDetailsList.associateBy(
                                    { it.productId },
                                    { it.oneTimePurchaseOfferDetails?.formattedPrice ?: "" }
                                )

                                //Actualizamos los packs con la información de compra y precio
                                val updatedPacks = packs.map { pack ->
                                    pack.copy(
                                        purchased =
                                        pack.packNumber == 0 || ownedPackages.contains(pack.packId),
                                        packPrice = pricesMap[pack.packId]?.replace(
                                            "\\s".toRegex(),
                                            replacement = ""
                                        ) ?: "?€"
                                    )
                                }

                                //Actualizamos el RecyclerView en el hilo principal
                                a.runOnUiThread {
                                    setUpQuestionsPack(updatedPacks)
                                }
                            } else error()
                        }
                    },
                    onError = { error() }
                )
            }
        }
    }

    private fun setUpQuestionsPack(packs: List<QuestionsPack>) {
        binding.rvPacks.apply {
            layoutManager = LinearLayoutManager(activity)
            questionsPackAdapter = QuestionsPackAdapter(packs, this@QuestionsPacksFragment)
            adapter = questionsPackAdapter
        }

        viewModel.getSelectedQuestionPack()
    }

    override fun selectPack(packNumber: Int) = viewModel.selectQuestionPack(packNumber)

    override fun seeMore(pack: QuestionsPack) {
        findNavController().navigate(actionToPackDetail(questionPack = pack, rangePack = null))
    }

    override fun purchase(packId: String) {
        billingManager?.startConnection {
            activity?.let { billingManager?.launchPurchase(it, packId) }
        }
    }

    override fun purchasedCompleted(packId: String) {
        requireActivity().runOnUiThread { questionsPackAdapter?.purchasedPack(packId) }
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}