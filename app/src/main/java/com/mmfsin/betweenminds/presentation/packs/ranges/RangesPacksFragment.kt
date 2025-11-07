package com.mmfsin.betweenminds.presentation.packs.ranges

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
import com.mmfsin.betweenminds.domain.models.RangesPack
import com.mmfsin.betweenminds.presentation.packs.PacksVPFragmentDirections.Companion.actionToPackDetail
import com.mmfsin.betweenminds.presentation.packs.manager.BillingManager
import com.mmfsin.betweenminds.presentation.packs.manager.IBillingListener
import com.mmfsin.betweenminds.presentation.packs.manager.SelectedManager
import com.mmfsin.betweenminds.presentation.packs.ranges.adapter.IRangesPackListener
import com.mmfsin.betweenminds.presentation.packs.ranges.adapter.RangesPackAdapter
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RangesPacksFragment(val areFree: Boolean) :
    BaseFragment<FragmentPacksBinding, RangesPacksViewModel>(),
    IRangesPackListener, IBillingListener {

    override val viewModel: RangesPacksViewModel by viewModels()
    private lateinit var mContext: Context

    private var billingManager: BillingManager? = null

    private var rangesPackAdapter: RangesPackAdapter? = null

    @Inject
    lateinit var selectedManager: SelectedManager

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPacksBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getRangesPack()
    }

    override fun setUI() {
        binding.loading.root.isVisible = true
    }

    override fun setListeners() {
        selectedManager.selectedRangesPackNumber.observe(viewLifecycleOwner) { packNumber ->
            packNumber?.let { rangesPackAdapter?.updateSelectedPack(it) }
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is RangesPacksEvent.SelectedPack -> {
                    rangesPackAdapter?.updateSelectedPack(event.selected)
                    binding.loading.root.isVisible = false
                }

                is RangesPacksEvent.RangesPacks -> {
                    if (areFree) allPacksFree(event.packs)
                    else checkPurchasedPacks(event.packs)
                }

                is RangesPacksEvent.NewPackSelected -> {
                    rangesPackAdapter?.updateSelectedPack(event.packNumber)
                }

                is RangesPacksEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun allPacksFree(packs: List<RangesPack>) {
        binding.apply {
            val freePacks = packs.map { pack -> pack.copy(purchased = true) }
            setUpRangesPack(freePacks)
        }
    }

    private fun checkPurchasedPacks(packs: List<RangesPack>) {
        activity?.let { a ->
            billingManager = BillingManager(a, this@RangesPacksFragment)
            billingManager?.startConnection {
                billingManager?.queryPurchasedIds(
                    onResult = { ownedPackages ->

                        val test = listOf("pack_ranges_1")

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
                                a.runOnUiThread { setUpRangesPack(updatedPacks) }
                            } else error()
                        }
                    },
                    onError = { error() }
                )
            }
        }
    }

    private fun setUpRangesPack(packs: List<RangesPack>) {
        binding.rvPacks.apply {
            layoutManager = LinearLayoutManager(activity)
            rangesPackAdapter = RangesPackAdapter(packs, this@RangesPacksFragment)
            adapter = rangesPackAdapter
        }
        viewModel.getSelectedRangePack()
    }

    override fun selectPack(packNumber: Int) = viewModel.selectRangesPack(packNumber)

    override fun seeMore(pack: RangesPack) {
        findNavController().navigate(actionToPackDetail(questionPack = null, rangePack = pack))
    }

    override fun purchase(packId: String) {
        billingManager?.startConnection {
            activity?.let { billingManager?.launchPurchase(it, packId) }
        }
    }

    override fun purchasedCompleted(packId: String) {
        activity?.runOnUiThread {
            rangesPackAdapter?.purchasedPack(packId)
        }
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}