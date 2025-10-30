package com.mmfsin.betweenminds.presentation.packs.ranges

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
import com.mmfsin.betweenminds.domain.models.RangesPack
import com.mmfsin.betweenminds.presentation.packs.manager.BillingManager
import com.mmfsin.betweenminds.presentation.packs.ranges.adapter.IRangesPackListener
import com.mmfsin.betweenminds.presentation.packs.ranges.adapter.RangesPackAdapter
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RangesPacksFragment : BaseFragment<FragmentPacksBinding, RangesPacksViewModel>(),
    IRangesPackListener {

    override val viewModel: RangesPacksViewModel by viewModels()
    private lateinit var mContext: Context

    private var billingManager: BillingManager? = null

    private var rangesPackAdapter: RangesPackAdapter? = null

    override fun inflateView(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPacksBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            billingManager = BillingManager(it)
            billingManager?.startConnection {
                billingManager?.queryPurchasedIds(
                    onResult = { ownedPackages ->
                        println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*")
                        println("purchased packages $ownedPackages")
                    },
                    onError = { error() }
                )
            }
        }
        viewModel.getRangesPack()
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
                is RangesPacksEvent.SelectedPack -> {
                    rangesPackAdapter?.updateSelectedPack(event.selected)
                    binding.loading.root.isVisible = false
                }

                is RangesPacksEvent.RangesPacks -> setUpRangesPack(event.packs)
                is RangesPacksEvent.NewPackSelected -> {
                    rangesPackAdapter?.updateSelectedPack(event.packNumber)
                }

                is RangesPacksEvent.SomethingWentWrong -> error()
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

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}