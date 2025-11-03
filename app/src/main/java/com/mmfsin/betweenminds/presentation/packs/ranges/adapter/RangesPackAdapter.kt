package com.mmfsin.betweenminds.presentation.packs.ranges.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemPackBinding
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.RangesPack

class RangesPackAdapter(
    private val packs: List<RangesPack>,
    private val listener: IRangesPackListener
) : RecyclerView.Adapter<RangesPackAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPackBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(pack: RangesPack, listener: IRangesPackListener) {
            binding.apply {
                Glide.with(c).load(pack.packIcon).into(ivPackIcon)
                tvPrice.text = pack.packPrice
                tvTitle.text = pack.packTitle
                tvDescription.text = pack.packDescription
                tvInclude.text = c.getString(R.string.pack_include_ranges_as)
                setUpRangesAdapter(pack.ranges)

                btnPurchase.button.text = c.getString(R.string.pack_purchase_btn)
                btnSelect.button.text = c.getString(R.string.pack_selected_btn)

                tvPrice.isVisible = !pack.purchased
                btnPurchase.root.isVisible = !pack.purchased

                if (!pack.purchased) {
                    btnSelect.root.isVisible = false
                    tvSelected.isVisible = false
                } else {
                    btnSelect.root.isVisible = !pack.selected
                    tvSelected.isVisible = pack.selected
                }

                btnSelect.button.setOnClickListener {
                    if (!pack.selected) listener.selectPack(pack.packNumber)
                }

                btnPurchase.button.setOnClickListener {

                }
            }
        }

        private fun setUpRangesAdapter(ranges: List<Range>) {
            binding.rvExamples.apply {
                layoutManager = LinearLayoutManager(c)
                adapter = RExamplesPackAdapter(ranges)
            }
        }
    }

    fun updateSelectedPack(packNumber: Int) {
        deletePreviousSelected(packNumber)
        var position: Int? = null
        packs.forEachIndexed { i, pack ->
            if (pack.packNumber == packNumber) {
                pack.selected = !pack.selected
                position = i
            }
        }
        position?.let { pos -> notifyItemChanged(pos) }
    }

    private fun deletePreviousSelected(packNumber: Int) {
        var position: Int? = null
        packs.forEachIndexed { i, pack ->
            if (pack.selected && pack.packNumber != packNumber) {
                pack.selected = false
                position = i
            }
        }
        position?.let { pos -> notifyItemChanged(pos) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_pack, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(packs[position], listener)
    }

    override fun getItemCount(): Int = packs.size
}