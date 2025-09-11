package com.mmfsin.betweenminds.presentation.scores.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ItemSavedScoreBinding
import com.mmfsin.betweenminds.domain.models.SavedScore

class SavedScoresAdapter(
    private val scores: List<SavedScore>,
) : RecyclerView.Adapter<SavedScoresAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSavedScoreBinding.bind(view)
        val c: Context = binding.root.context
        fun bind(score: SavedScore) {
            binding.apply {
                tvPlayerOne.text = score.playerOneName
                tvPlayerTwo.text = score.playerTwoName
                tvDate.text = score.date

                val points = "${score.points}"
                tvPoints.text = points

                if (score.notes.isEmpty()) tvNotes.isVisible = false
                else tvNotes.text = score.notes
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_saved_score, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scores[position])
    }

    override fun getItemCount(): Int = scores.size
}