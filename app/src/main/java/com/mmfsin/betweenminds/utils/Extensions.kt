package com.mmfsin.betweenminds.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.slider.Slider
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.dialog.ErrorDialog
import com.mmfsin.betweenminds.databinding.IncludePeopleBinding
import com.mmfsin.betweenminds.domain.models.ScoreQuestion
import com.mmfsin.betweenminds.domain.models.ScoreRange
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Locale
import java.util.concurrent.TimeUnit

fun FragmentActivity.showErrorDialog(goBack: Boolean = true) {
    val dialog = ErrorDialog(goBack)
    this.let { dialog.show(it.supportFragmentManager, "") }
}

fun Activity.closeKeyboard() {
    this.currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Context.closeKeyboardFromDialog() {
    val imm: InputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if (imm.isActive) imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun countDown(millis: Long, action: () -> Unit) {
    object : CountDownTimer(millis, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            action()
        }
    }.start()
}

fun FragmentActivity?.showFragmentDialog(dialog: DialogFragment) =
    this?.let { dialog.show(it.supportFragmentManager, "") }

fun View.animateY(pos: Float, duration: Long, endAction: () -> Unit = {}) =
    this.animate().translationY(pos).setDuration(duration).withEndAction { endAction() }

fun View.animateX(pos: Float, duration: Long, endAction: () -> Unit = {}) =
    this.animate().translationX(pos).setDuration(duration).withEndAction { endAction() }

fun View.hideAlpha(duration: Long, onEnd: () -> Unit = {}) =
    this.animate().alpha(0f).setDuration(duration).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            onEnd()
        }
    })

fun View.handleAlpha(alpha: Float, duration: Long, onEnd: () -> Unit = {}) =
    this.animate().alpha(alpha).setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onEnd()
            }
        })

fun View.showAlpha(duration: Long, onEnd: () -> Unit = {}) =
    this.animate().alpha(1f).setDuration(duration).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            onEnd()
        }
    })

fun View.showCustomAlpha(alpha: Float, duration: Long) =
    this.animate().alpha(alpha).setDuration(duration)

fun Dialog.animateDialog() {
    val dialogView = this.window?.decorView
    dialogView?.let {
        it.scaleX = 0f
        it.scaleY = 0f
        val scaleXAnimator = ObjectAnimator.ofFloat(it, View.SCALE_X, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(it, View.SCALE_Y, 1f)
        AnimatorSet().apply {
            playTogether(scaleXAnimator, scaleYAnimator)
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }
}

fun setExpandableView(expandable: View, linear: LinearLayout) {
    val v = if (expandable.isVisible) View.GONE else View.VISIBLE
    TransitionManager.beginDelayedTransition(linear, AutoTransition())
    expandable.visibility = v
}

fun <T1 : Any, T2 : Any, R : Any> checkNotNulls(p1: T1?, p2: T2?, block: (T1, T2) -> R): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

fun encodeToBase64(input: String): String {
    val bytes = input.toByteArray(Charsets.UTF_8)
    return Base64.getEncoder().encodeToString(bytes)
}

fun decodeFromBase64(encoded: String): String {
    val bytes = Base64.getDecoder().decode(encoded)
    return String(bytes, Charsets.UTF_8)
}

fun Context.setGlideImage(image: String, view: ImageView, loading: ImageView? = null) {
    Glide.with(this).load(image).listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
        ): Boolean {
//            loading?.setImageResource(R.drawable.ic_loading_error)
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            loading?.visibility = View.INVISIBLE
            return false
        }
    }).into(view)
}

fun getNumberColor(value: Int): Int {
    return if (value >= 50) R.color.dark_orange
//    else if (value == 50) R.color.dark_grey
    else R.color.blue
}

fun Slider.moveSliderValue(value: Int, duration: Long = 500, onEnd: () -> Unit = {}) {
    val animator = ValueAnimator.ofInt(this.value.toInt(), value)
    animator.duration = duration
    animator.addUpdateListener { anim ->
        this.value = (anim.animatedValue as Int).toFloat()
    }
    animator.doOnEnd { onEnd() }
    animator.start()
}

fun getEmptyScoreQuestionList() =
    listOf(ScoreQuestion(), ScoreQuestion(), ScoreQuestion(), ScoreQuestion())

fun getEmptyScoreRangesList() = listOf(ScoreRange(), ScoreRange(), ScoreRange(), ScoreRange())

fun getQuestionModePoints(num1: Int?, num2: Int?): Int? {
    if (num1 == null || num2 == null) return null
    else {
        val diff = kotlin.math.abs(num1 - num2)
        return when {
            diff > 15 -> 0
            diff == 0 -> 20
            else -> 16 - diff
        }
    }
}

fun getTodayDate(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    return today.format(formatter)
}

fun Context.handleSliderTrackColor(value: Int, slider: Slider) {
    val color = if (value > 50) R.color.dark_orange
    else if (value == 50) R.color.dark_grey
    else R.color.blue

    val colorStateList = ColorStateList.valueOf(getColor(this, color))
    slider.trackTintList = colorStateList
}

fun getKonfettiParty() = Party(
    speed = 0f,
    maxSpeed = 30f,
    damping = 0.9f,
    spread = 360,
    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
    position = Position.Relative(0.5, 0.3)
)

fun updatePercents(people: IncludePeopleBinding, phase: Int, percentX: Int) {
    people.apply {
        val percentLeft = "${(100 - percentX)}"
        val percentRight = "$percentX"
        if (phase == 1) {
            percentOneBlue.text = percentLeft
            percentOneOrange.text = percentRight
        } else {
            percentTwoBlue.text = percentLeft
            percentTwoOrange.text = percentRight
        }
        moveHumans(this, percentX)
    }
}

fun scaleHumans(binding: IncludePeopleBinding, value: Int) {
    binding.apply {
        val factor = value / 100f

//        val leftScale = 2f - factor
//        val rightScale = 1f + factor
        val leftScale = 3f - factor * 2f
        val rightScale = 1f + factor * 2f

        ivRight.scaleX = leftScale
        ivRight.scaleY = leftScale

        ivLeft.scaleX = rightScale
        ivLeft.scaleY = rightScale
    }
}

fun moveHumans(binding: IncludePeopleBinding, value: Int) {
    binding.apply {
        if (value < 50) {
            ivLeft.setImageResource(R.drawable.ic_human_up)
            ivRight.setImageResource(R.drawable.ic_human_down)
        } else if (value == 50) {
            ivLeft.setImageResource(R.drawable.ic_human_down)
            ivRight.setImageResource(R.drawable.ic_human_down)
        } else {
            ivLeft.setImageResource(R.drawable.ic_human_down)
            ivRight.setImageResource(R.drawable.ic_human_up)
        }
    }
}

fun handlePercentsPlayerOne(people: IncludePeopleBinding, show: Boolean) {
    people.apply {
        if (show) {
            icQuestionOneBlue.hideAlpha(350) { llPercentOneBlue.showAlpha(350) }
            icQuestionOneOrange.hideAlpha(350) { llPercentOneOrange.showAlpha(350) }
        } else {
            llPercentOneBlue.hideAlpha(350) { icQuestionOneBlue.showAlpha(350) }
            llPercentOneOrange.hideAlpha(350) { icQuestionOneOrange.showAlpha(350) }
        }
    }
}

fun handlePercentsPlayerTwo(people: IncludePeopleBinding, show: Boolean) {
    people.apply {
        if (show) {
            icQuestionTwoBlue.hideAlpha(350) { llPercentTwoBlue.showAlpha(350) }
            icQuestionTwoOrange.hideAlpha(350) { llPercentTwoOrange.showAlpha(350) }
        } else {
            llPercentTwoBlue.hideAlpha(100) { icQuestionTwoBlue.showAlpha(100) }
            llPercentTwoOrange.hideAlpha(100) { icQuestionTwoOrange.showAlpha(100) }
        }
    }
}


//fun FragmentActivity.shouldShowInterstitial(position: Int) =
//    (this as MainActivity).showInterstitial(position)
