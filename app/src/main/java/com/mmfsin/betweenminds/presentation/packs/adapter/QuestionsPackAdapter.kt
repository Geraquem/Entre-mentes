package com.mmfsin.betweenminds.presentation.packs.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemPackQuestionsBinding
import com.mmfsin.betweenminds.domain.models.QuestionPack

class QuestionsPackAdapter(
    private val packs: List<QuestionPack>,
) : RecyclerView.Adapter<QuestionsPackAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPackQuestionsBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(pack: QuestionPack) {
            binding.apply {
                tvTitle.text = c.getString(pack.packTitle)
                tvDescription.text = c.getString(pack.packDescription)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_pack_questions, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(packs[position])
    }

    override fun getItemCount(): Int = packs.size
}