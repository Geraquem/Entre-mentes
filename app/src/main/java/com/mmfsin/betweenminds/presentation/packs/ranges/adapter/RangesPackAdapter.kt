package com.mmfsin.betweenminds.presentation.packs.ranges.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                tvTitle.text = pack.packTitle
                tvDescription.text = pack.packDescription

                setUpQuestionsAdapter(pack.ranges)

                ivSelected.isVisible = pack.selected

                root.setOnClickListener { listener.selectPack(pack.packNumber) }
            }
        }

        private fun setUpQuestionsAdapter(ranges: List<Range>) {
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