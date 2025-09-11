package com.mmfsin.betweenminds.presentation.scores

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmfsin.betweenminds.base.BaseFragment
import com.mmfsin.betweenminds.base.bedrock.BedRockActivity
import com.mmfsin.betweenminds.databinding.FragmentSavedScoresBinding
import com.mmfsin.betweenminds.domain.models.SavedScore
import com.mmfsin.betweenminds.presentation.scores.adapter.SavedScoresAdapter
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedScoresFragment : BaseFragment<FragmentSavedScoresBinding, SavedScoresViewModel>() {

    override val viewModel: SavedScoresViewModel by viewModels()
    private lateinit var mContext: Context

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSavedScoresBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSavedScores()
    }

    override fun setUI() {
        binding.apply {
            (activity as BedRockActivity).setUpToolbar(
                instructionsVisible = false
            )
            loading.root.isVisible = true
        }
    }

    override fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is SavedScoresEvent.Scores -> setUpScores(event.scores)
                is SavedScoresEvent.SomethingWentWrong -> error()
            }
        }
    }

    private fun setUpScores(savedScores: List<SavedScore>) {
        binding.apply {
            rvScores.apply {
                layoutManager = LinearLayoutManager(mContext)
                adapter = SavedScoresAdapter(savedScores)
            }
            loading.root.isVisible = false
        }
    }

    private fun error() = activity?.showErrorDialog()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}

