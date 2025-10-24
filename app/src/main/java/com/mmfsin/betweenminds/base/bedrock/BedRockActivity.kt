package com.mmfsin.betweenminds.base.bedrock

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.databinding.ActivityBedrockBinding
import com.mmfsin.betweenminds.utils.BEDROCK_BOOLEAN_ARGS
import com.mmfsin.betweenminds.utils.BEDROCK_STR_ARGS
import com.mmfsin.betweenminds.utils.ROOT_ACTIVITY_NAV_GRAPH
import com.mmfsin.betweenminds.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BedRockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBedrockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBedrockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeStatusBarColor(R.color.background_black)
        respectScreenDimensions()

        setUpNavGraph()
        setAds()
    }

    private fun respectScreenDimensions() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                systemBarsInsets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun changeStatusBarColor(color: Int) {
        // Android 15+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
                view.setBackgroundColor(ContextCompat.getColor(this, color))
                view.setPadding(0, statusBarInsets.top, 0, 0)
                insets
            }

        } else {
            // For Android 14 and below
            @Suppress("DEPRECATION")
            window.statusBarColor = ContextCompat.getColor(this, color)
        }

        //true == dark
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }

    private fun setUpNavGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.br_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = intent.getIntExtra(ROOT_ACTIVITY_NAV_GRAPH, -1)
        navController.apply { if (navGraph != -1) setGraph(navGraph) else error() }
    }

    fun openBedRockActivity(
        navGraph: Int,
        strArgs: String? = null,
        booleanArgs: Boolean? = null
    ) {
        val intent = Intent(this, BedRockActivity::class.java)
        strArgs?.let { intent.putExtra(BEDROCK_STR_ARGS, strArgs) }
        booleanArgs?.let { intent.putExtra(BEDROCK_BOOLEAN_ARGS, booleanArgs) }
        intent.putExtra(ROOT_ACTIVITY_NAV_GRAPH, navGraph)
        startActivity(intent)
    }

    private fun setAds() {
//        val adRequest = AdRequest.Builder().build()
//        binding.adBanner.loadAd(adRequest)
        showBanner(visible = false)
//
//        binding.adBanner.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                println("Banner cargado correctamente")
//            }
//
//            override fun onAdFailedToLoad(error: LoadAdError) {
//                print("Fallo al cargar el banner: ${error.message}")
//            }
//        }
    }

    fun showBanner(visible: Boolean = false) {
        binding.adBanner.isVisible = visible
    }

    private fun error() = showErrorDialog(goBack = true)
}