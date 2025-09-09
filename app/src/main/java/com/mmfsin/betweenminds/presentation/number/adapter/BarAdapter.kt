package com.mmfsin.betweenminds.presentation.number.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmfsin.betweenminds.R

class BarAdapter(private val count: Int) : RecyclerView.Adapter<BarAdapter.BarViewHolder>() {

    class BarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bar: View = view.findViewById(R.id.bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.include_curtain, parent, false)
        return BarViewHolder(view)
    }

    override fun getItemCount(): Int = count

    override fun onBindViewHolder(holder: BarViewHolder, position: Int) {}
}