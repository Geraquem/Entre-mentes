package com.mmfsin.betweenminds.presentation.common

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.google.android.material.slider.Slider

class HalfTrackSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.sliderStyle
) : Slider(context, attrs, defStyleAttr) {

    var leftColor: Int = Color.parseColor("#EAAC50")
    var rightColor: Int = Color.parseColor("#1D5FC3")
    var trackCornerRadius: Float = 12f // en px

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        // Dibujamos el track completo redondeado
        val trackHeight = trackHeight.toFloat()
        val cx = 50 // posición horizontal del track
        val cy = height / 2f
        val left = paddingStart.toFloat()
        val right = width - paddingEnd.toFloat()
        val top = cy - trackHeight / 2
        val bottom = cy + trackHeight / 2

        val mid = left + (right - left) / 2f

        // izquierda
        paint.color = leftColor
        canvas.drawRoundRect(RectF(left, top, mid, bottom), trackCornerRadius, trackCornerRadius, paint)

        // derecha
        paint.color = rightColor
        canvas.drawRoundRect(RectF(mid, top, right, bottom), trackCornerRadius, trackCornerRadius, paint)

        super.onDraw(canvas) // dibuja thumb y halo
    }
}
