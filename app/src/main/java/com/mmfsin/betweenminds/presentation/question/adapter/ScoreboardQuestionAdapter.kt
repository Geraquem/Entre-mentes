package com.mmfsin.betweenminds.presentation.question.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemScoreQuestionBinding
import com.mmfsin.betweenminds.domain.models.ScoreQuestion
import com.mmfsin.betweenminds.utils.hideAlpha
import com.mmfsin.betweenminds.utils.showAlpha
import kotlin.math.absoluteValue

class ScoreboardQuestionAdapter(
    private val scores: List<ScoreQuestion>,
) : RecyclerView.Adapter<ScoreboardQuestionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemScoreQuestionBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(data: ScoreQuestion, position: Int) {
            binding.apply {
                tvRound.text = "$position"
                if (data.discovered) discovered.hideAlpha(500)
                else discovered.showAlpha(1)

                data.topNumber?.first?.let { setSliderNumbers(tvTopLeftNumber, it) }
                data.topNumber?.second?.let { setSliderNumbers(tvTopRightNumber, it) }
                data.bottomNumber?.first?.let { setSliderNumbers(tvBottomLeftNumber, it) }
                data.bottomNumber?.second?.let { setSliderNumbers(tvBottomRightNumber, it) }
                data.points?.let { tvPoints.text = "$it" }
            }
        }

        private fun setSliderNumbers(textView: TextView, number: Int?) {
            number?.let {
                textView.text = "${number.absoluteValue}"
//                textView.setTextColor(getColor(c, getNumberColor(number)))
            }
        }
    }

    fun updateScore(newScore: ScoreQuestion, position: Int) {
        val score = scores[position]
        score.discovered = newScore.discovered
        score.topNumber = newScore.topNumber
        score.bottomNumber = newScore.bottomNumber
        score.points = newScore.points
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetScores() {
        scores.forEach {
            it.discovered = false
            it.topNumber = Pair(0, 0)
            it.bottomNumber = Pair(0, 0)
        }
        notifyDataSetChanged()
    }

    fun getTotalPoints(): Int {
        var totalPoints = 0
        scores.forEach {
            it.points?.let { points -> totalPoints += points }
        }
        return totalPoints
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_score_question, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scores[position], position + 1)
    }

    override fun getItemCount(): Int = scores.size
}