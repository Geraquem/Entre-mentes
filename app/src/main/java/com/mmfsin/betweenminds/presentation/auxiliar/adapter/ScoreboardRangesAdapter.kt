package com.mmfsin.betweenminds.presentation.auxiliar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemScoreRangeBinding
import com.mmfsin.betweenminds.domain.models.ScoreRange
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha

class ScoreboardRangesAdapter(
    private val scoreRanges: List<ScoreRange>,
) : RecyclerView.Adapter<ScoreboardRangesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemScoreRangeBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(data: ScoreRange, position: Int, hideBarrier: Boolean) {
            binding.apply {
                if(hideBarrier) barrier.visibility = View.GONE

                tvRound.text = "$position"
                if (data.discovered) discovered.hideAlpha(500)
                else discovered.showAlpha(1)

                if (data.activeRound) tvRound.setTextColor(getColor(c, R.color.dark_red))
                else tvRound.setTextColor(getColor(c, R.color.dark_grey))

                data.points?.let { p ->
                    val color = when (p) {
                        2 -> R.color.dark_orange
                        5 -> R.color.green
                        else -> R.color.dark_grey
                    }
                    tvPoints.setTextColor(getColor(c, color))
                    tvPtsText.setTextColor(getColor(c, color))
                    tvPoints.text = "$p"
                }
            }
        }
    }

    fun updateScore(newScoreRange: ScoreRange, position: Int) {
        val score = scoreRanges[position]
        score.discovered = newScoreRange.discovered
        score.points = newScoreRange.points
        notifyItemChanged(position)
    }

    fun roundColor(position: Int) {
        val score = scoreRanges[position]
        if (!score.discovered) score.activeRound = true
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetScores() {
        scoreRanges.forEach {
            it.discovered = false
            it.activeRound = false
            it.points = 0
        }
        notifyDataSetChanged()
    }

    fun getTotalPoints(): Int {
        var totalPoints = 0
        scoreRanges.forEach {
            it.points?.let { points -> totalPoints += points }
        }
        return totalPoints
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_score_range, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scoreRanges[position], position + 1, (position == itemCount - 1))
    }

    override fun getItemCount(): Int = scoreRanges.size
}