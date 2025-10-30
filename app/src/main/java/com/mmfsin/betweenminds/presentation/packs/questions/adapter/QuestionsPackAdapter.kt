package com.mmfsin.betweenminds.presentation.packs.questions.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemPackBinding
import com.mmfsin.betweenminds.domain.models.QuestionPack

class QuestionsPackAdapter(
    private val packs: List<QuestionPack>,
    private val listener: IQuestionsPackListener
) : RecyclerView.Adapter<QuestionsPackAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPackBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(pack: QuestionPack, listener: IQuestionsPackListener) {
            binding.apply {
                tvTitle.text = pack.packTitle
                tvDescription.text = pack.packDescription

                setUpQuestionsAdapter(pack.questions.map { it.question })

                ivSelected.isVisible = pack.selected

                root.setOnClickListener { listener.selectPack(pack.packNumber) }
            }
        }

        private fun setUpQuestionsAdapter(questions: List<String>) {
            binding.rvExamples.apply {
                layoutManager = LinearLayoutManager(c)
                adapter = QExamplesPackAdapter(questions)
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