package com.mmfsin.betweenminds.presentation.questions.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemScoreBinding
import com.mmfsin.betweenminds.domain.models.ScoreQuestion
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha

class ScoreboardQuestionAdapter(
    private val scores: List<ScoreQuestion>,
) : RecyclerView.Adapter<ScoreboardQuestionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemScoreBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(data: ScoreQuestion, position: Int, hideBarrier: Boolean) {
            binding.apply {
                if (hideBarrier) barrier.visibility = View.GONE

                if (data.activeRound) tvRound.setTextColor(getColor(c, R.color.dark_red))
                else tvRound.setTextColor(getColor(c, R.color.dark_grey))

                tvRound.text = "$position"
                if (data.discovered) discovered.hideAlpha(500)
                else discovered.showAlpha(1)
                data.points?.let { tvPoints.text = "$it" }
            }
        }
    }

    fun updateScore(newScore: ScoreQuestion, position: Int) {
        val score = scores[position]
        score.discovered = newScore.discovered
        score.actualQuestion = newScore.actualQuestion
        score.topNumbers = newScore.topNumbers
        score.bottomNumbers = newScore.bottomNumbers
        score.points = newScore.points
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetScores() {
        scores.forEach {
            it.discovered = false
            it.actualQuestion = null
            it.topNumbers = null
            it.bottomNumbers = null
            it.activeRound = false
        }
        notifyDataSetChanged()
    }

    fun getTotalData(): List<ScoreQuestion> = scores

    fun roundColor(position: Int) {
        val score = scores[position]
        if (!score.discovered) score.activeRound = true
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scores[position], position + 1, (position == itemCount - 1))
    }

    override fun getItemCount(): Int = scores.size
}