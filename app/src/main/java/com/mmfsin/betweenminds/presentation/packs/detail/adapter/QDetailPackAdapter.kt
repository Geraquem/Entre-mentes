package com.mmfsin.betweenminds.presentation.packs.detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemPackQuestionsExampleBinding

class QDetailPackAdapter(
    private val questions: List<String>
) : RecyclerView.Adapter<QDetailPackAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPackQuestionsExampleBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(question: String) {
            binding.tvQuestion.text = question
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pack_questions_detail, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount(): Int = questions.size
}