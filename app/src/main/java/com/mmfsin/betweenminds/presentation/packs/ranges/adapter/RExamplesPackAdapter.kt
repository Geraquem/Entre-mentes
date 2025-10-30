package com.mmfsin.betweenminds.presentation.packs.ranges.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemPackRangesExampleBinding
import com.mmfsin.betweenminds.domain.models.Range

class RExamplesPackAdapter(
    private val ranges: List<Range>
) : RecyclerView.Adapter<RExamplesPackAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPackRangesExampleBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(range: Range) {
            binding.apply {
                tvRangeLeft.text = range.leftRange
                tvRangeRight.text = range.rightRange
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pack_ranges_example, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ranges[position])
    }

    override fun getItemCount(): Int = ranges.size
}