package com.mmfsin.betweenminds.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
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
import java.util.Base64

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
    return if (value > 0) R.color.blue
    else if (value == 0) R.color.dark_grey
    else R.color.dark_orange
}

fun Slider.moveSliderValue(value: Float) {
    val animator = ValueAnimator.ofFloat(this.value, value)
    animator.duration = 500
    animator.addUpdateListener { anim ->
        this.value = anim.animatedValue as Float
    }
    animator.start()
}

//fun FragmentActivity.shouldShowInterstitial(position: Int) =
//    (this as MainActivity).showInterstitial(position)
