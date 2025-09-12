package com.mmfsin.betweenminds.presentation.common.dialogs.save

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.base.BaseDialog
import com.mmfsin.betweenminds.databinding.DialogSavePointsBinding
import com.mmfsin.betweenminds.domain.models.SavedScore
import com.mmfsin.betweenminds.utils.getTodayDate
import com.mmfsin.betweenminds.utils.showAlpha
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class SavePointsDialog(
    private val mode: String,
    private val points: Int,
    private val restartGame: () -> Unit,
    private val exit: () -> Unit,
) : BaseDialog<DialogSavePointsBinding>() {

    private val viewModel: SavePointsViewModel by viewModels()

    override fun inflateView(inflater: LayoutInflater) = DialogSavePointsBinding.inflate(inflater)

    override fun setCustomViewDialog(dialog: Dialog) = centerViewDialog(dialog)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observe()
    }

    override fun setUI() {
        isCancelable = false
        binding.apply {
            clResult.alpha = 0f

            btnSave.isEnabled = true
            btnSave.alpha = 1f

            tvError.isVisible = false

            tvPoints.text = if (points == 1) getString(R.string.endgame_one_point)
            else getString(R.string.endgame_pts, "$points")
        }
    }

    override fun setListeners() {
        binding.apply {
            btnExit.setOnClickListener {
                exit()
                dismiss()
            }

            btnSave.setOnClickListener {
                val pyOne = etPlayerOne.text.toString()
                val pyTwo = etPlayerTwo.text.toString()
                if (pyOne.isNotEmpty() && pyTwo.isNotEmpty()) {
                    tvError.isVisible = false
                    btnSave.isEnabled = false

                    val newScore = SavedScore(
                        id = UUID.randomUUID().toString(),
                        playerOneName = pyOne,
                        playerTwoName = pyTwo,
                        points = points,
                        notes = etNotes.text.toString(),
                        date = getTodayDate(),
                        mode = mode
                    )
                    viewModel.saveNewScore(newScore)
                } else {
                    tvError.isVisible = true
                }
            }

            btnRematch.setOnClickListener {
                restartGame()
                dismiss()
            }
        }
    }

    private fun observe() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is SavePointsEvent.Completed -> {
                    binding.apply {
                        llData.visibility = View.INVISIBLE
                        btnSave.alpha = 0.4f
                        clResult.showAlpha(500)
                    }
                }

                is SavePointsEvent.SomethingWentWrong -> {
                    Toast.makeText(requireContext(), "ERRRRRORRR", Toast.LENGTH_SHORT).show()
                    //                    exit()
                }
            }
        }
    }
}