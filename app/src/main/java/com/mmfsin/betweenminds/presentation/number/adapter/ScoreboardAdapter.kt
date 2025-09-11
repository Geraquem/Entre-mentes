package com.mmfsin.betweenminds.presentation.number.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemScoreBinding
import com.mmfsin.betweenminds.domain.models.Score
import com.mmfsin.betweenminds.utils.getNumberColor
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import kotlin.math.absoluteValue

class ScoreboardAdapter(
    private val scores: List<Score>,
) : RecyclerView.Adapter<ScoreboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemScoreBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(data: Score, position: Int) {
            binding.apply {
                tvRound.text = "$position"
                if (data.discovered) discovered.hideAlpha(500)
                else discovered.showAlpha(1)

                setSliderNumbers(tvTopNumber, data.topNumber)
                setSliderNumbers(tvBottomNumber, data.resultNumber)
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

    fun updateScore(newScore: Score, position: Int) {
        val score = scores[position]
        score.discovered = newScore.discovered
        score.topNumber = newScore.topNumber
        score.resultNumber = newScore.resultNumber
        score.points = newScore.points
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scores[position], position + 1)
    }

    override fun getItemCount(): Int = scores.size
}