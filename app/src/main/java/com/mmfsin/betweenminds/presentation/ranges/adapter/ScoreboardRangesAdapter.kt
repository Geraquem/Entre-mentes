package com.mmfsin.betweenminds.presentation.ranges.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemScoreRangeBinding
import com.mmfsin.betweenminds.domain.models.ScoreRange
import com.mmfsin.betweenminds.utils.getNumberColor
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import kotlin.math.absoluteValue

class ScoreboardRangesAdapter(
    private val scoreRanges: List<ScoreRange>,
) : RecyclerView.Adapter<ScoreboardRangesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemScoreRangeBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(data: ScoreRange, position: Int) {
            binding.apply {
                tvRound.text = "$position"
                if (data.discovered) discovered.hideAlpha(500)
                else discovered.showAlpha(1)

                setSliderNumbers(tvTopNumber, data.topNumber)
                setSliderNumbers(tvBottomNumber, data.bottomNumber)
                data.points?.let { tvPoints.text = "$it" }
            }
        }

        private fun setSliderNumbers(textView: TextView, number: Int?) {
            number?.let {
                textView.text = "${number.absoluteValue}"
                textView.setTextColor(getColor(c, getNumberColor(number)))
            }
        }
    }

    fun updateScore(newScoreRange: ScoreRange, position: Int) {
        val score = scoreRanges[position]
        score.discovered = newScoreRange.discovered
        score.topNumber = newScoreRange.topNumber
        score.bottomNumber = newScoreRange.bottomNumber
        score.points = newScoreRange.points
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetScores() {
        scoreRanges.forEach {
            it.discovered = false
            it.topNumber = 0
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
            LayoutInflater.from(parent.context).inflate(R.layout.item_score_range, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scoreRanges[position], position + 1)
    }

    override fun getItemCount(): Int = scoreRanges.size
}