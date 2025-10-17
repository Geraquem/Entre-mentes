package com.mmfsin.betweenminds.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.dialog.ErrorDialog
import com.mmfsin.betweenminds.databinding.IncludePeopleBinding
import com.mmfsin.betweenminds.databinding.IncludeWaitingOtherPlayerBinding
import com.mmfsin.betweenminds.domain.models.ScoreQuestion
import com.mmfsin.betweenminds.domain.models.ScoreRange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
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

fun Fragment.countDown(millis: Long, action: () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        delay(millis)
        action()
    }
}

fun FragmentActivity?.showFragmentDialog(dialog: DialogFragment) =
    this?.let { dialog.show(it.supportFragmentManager, "") }

fun View.animateY(pos: Float, duration: Long, onEnd: () -> Unit = {}) =
    this.animate().translationY(pos).setDuration(duration).withEndAction {
        if (isAttachedToWindow) onEnd()
    }

fun View.animateX(pos: Float, duration: Long, onEnd: () -> Unit = {}) =
    this.animate().translationX(pos).setDuration(duration).withEndAction {
        if (isAttachedToWindow) onEnd()
    }

fun View.hideAlpha(duration: Long, onEnd: () -> Unit = {}) =
    this.animate().alpha(0f).setDuration(duration).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            if (isAttachedToWindow) onEnd()
        }
    })

fun View.handleAlpha(alpha: Float, duration: Long, onEnd: () -> Unit = {}) =
    this.animate().alpha(alpha).setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (isAttachedToWindow) onEnd()
            }
        })

fun View.showAlpha(duration: Long, onEnd: () -> Unit = {}) =
    this.animate().alpha(1f).setDuration(duration).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            if (isAttachedToWindow) onEnd()
        }
    })

fun <T1 : Any, T2 : Any, R : Any> checkNotNulls(p1: T1?, p2: T2?, block: (T1, T2) -> R): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

fun getEmptyScoreQuestionList() =
    listOf(ScoreQuestion(), ScoreQuestion(), ScoreQuestion(), ScoreQuestion())

fun getEmptyScoreRangesList() = listOf(ScoreRange(), ScoreRange(), ScoreRange(), ScoreRange())
fun getEmptyOScoreRangesList() = listOf(ScoreRange(), ScoreRange(), ScoreRange())

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

fun waitingPartnerVisibility(
    waitingView: IncludeWaitingOtherPlayerBinding,
    isVisible: Boolean
) {
    val w = waitingView.root
    if (isVisible) {
        w.visibility = View.VISIBLE
        w.showAlpha(500)
    } else w.hideAlpha(250) { w.visibility = View.INVISIBLE }
}

//fun FragmentActivity.shouldShowInterstitial(position: Int) =
//    (this as MainActivity).showInterstitial(position)
