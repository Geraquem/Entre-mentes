package com.mmfsin.betweenminds.presentation.question.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemEndQuestionBinding
import com.mmfsin.betweenminds.domain.models.ScoreQuestion

class QuestionAnswersAdapter(
    private val scores: List<ScoreQuestion>,
) : RecyclerView.Adapter<QuestionAnswersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemEndQuestionBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(data: ScoreQuestion, position: Int) {
            binding.apply {
                val pos = "$position."
                tvPosition.text = pos
                tvQuestion.text = data.actualQuestion
                data.topNumbers?.let {
                    it.first?.let { p -> setPercent(tvPercentOneBlue, p) }
                    it.second?.let { p -> setPercent(tvPercentOneOrange, p) }
                }
                data.bottomNumbers?.let {
                    it.first?.let { p -> setPercent(tvPercentTwoBlue, p) }
                    it.second?.let { p -> setPercent(tvPercentTwoOrange, p) }
                }
            }
        }

        private fun setPercent(tv: TextView, percent: Int) {
            val txt = "$percent%"
            tv.text = txt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_end_question, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scores[position], position + 1)
    }

    override fun getItemCount(): Int = scores.size
}