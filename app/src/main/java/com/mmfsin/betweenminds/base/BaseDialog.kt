package com.mmfsin.betweenminds.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.mmfsin.betweenminds.R

abstract class BaseDialog<VB : ViewBinding> : DialogFragment() {

    private var _binding: ViewBinding? = null

    @Suppress("UNCHECKED_CAST")
    protected val binding
        get() = _binding as VB

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = this.activity ?: return super.onCreateDialog(savedInstanceState)
        val dialog = Dialog(activity)
        _binding = inflateView(activity.layoutInflater)
        dialog.setContentView(binding.root)
        setCustomViewDialog(dialog)
        isCancelable = false
        dialog.show()
        return dialog
    }

    protected abstract fun inflateView(inflater: LayoutInflater): VB

    open fun setCustomViewDialog(dialog: Dialog) {}

    fun centerViewDialog(dialog: Dialog) {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = resources.displayMetrics.widthPixels * 0.9
        dialog.window?.setLayout(width.toInt(), WRAP_CONTENT)
    }

    fun centerCustomViewDialog(dialog: Dialog) {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
    }

    fun bottomViewDialog(dialog: Dialog) {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
        dialog.window?.attributes?.gravity = Gravity.BOTTOM
        dialog.window?.attributes?.windowAnimations = R.style.slide_up_down_dialog
    }

    fun bottomCustomViewDialog(dialog: Dialog, percent: Double) {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val height = resources.displayMetrics.heightPixels * percent
        dialog.window?.setLayout(MATCH_PARENT, height.toInt())
        dialog.window?.attributes?.gravity = Gravity.BOTTOM
        dialog.window?.attributes?.windowAnimations = R.style.slide_up_down_dialog
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val fragment = manager.findFragmentByTag(tag)
        val ft = manager.beginTransaction()
        if (fragment != null) {
            ft.remove(fragment)
            ft.commitAllowingStateLoss()
        }
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            Log.e(BaseDialog::class.java.name, e.stackTraceToString())
        }
    }

    override fun onStart() {
        super.onStart()
        setUI()
        setListeners()
    }

    open fun setUI() {}
    open fun setListeners() {}
}