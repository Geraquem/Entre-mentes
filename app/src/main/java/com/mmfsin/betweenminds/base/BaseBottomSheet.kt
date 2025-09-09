package com.mmfsin.betweenminds.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet<VB : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: ViewBinding? = null

    private val openToPercent: Double? = null

    @Suppress("UNCHECKED_CAST")
    protected val binding
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateView(inflater)
        return binding.root
    }

    protected abstract fun inflateView(inflater: LayoutInflater): VB

    override fun onStart() {
        super.onStart()
        setUI()
        setListeners()
    }

    open fun setUI() {}
    open fun setListeners() {}
}