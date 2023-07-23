package com.example.samplekotapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class PageWithInterstitialAds : AppCompatActivity() {
    var mInterstitialAd: InterstitialAd? = null
    var placement: Array<String> = AppBrodaPlacementHandler.loadPlacements("interstitialAds")
    var interstitialIndex = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_interstitial_ads)
        val adRequest = AdRequest.Builder().build()
        loadInterstitialAd(adRequest, placement)
        val showAdButton = findViewById<Button>(R.id.showInterstitialAd)
        showAdButton.setOnClickListener {
            if (mInterstitialAd != null) {
                mInterstitialAd!!.show(this@PageWithInterstitialAds)
            } else {
            }
        }
    }

    private fun loadInterstitialAd(adRequest: AdRequest?, placement: Array<String>) {
        if (placement.isEmpty()) //wrapper logic to handle errors
            return
        InterstitialAd.load(this, placement[interstitialIndex]!!, adRequest!!,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Toast.makeText(
                        this@PageWithInterstitialAds,
                        "Interstitial Ad Loaded @index: $interstitialIndex", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Toast.makeText(
                        this@PageWithInterstitialAds,
                        "Interstitial Ad Loading failed @index: $interstitialIndex",
                        Toast.LENGTH_SHORT
                    ).show()
                    mInterstitialAd = null
                    loadNextAd(adRequest, placement)
                }
            })
    }

    fun loadNextAd(adRequest: AdRequest?, placement: Array<String>) {
        if (interstitialIndex == placement.size) {
            interstitialIndex = 0
            return
        }
        interstitialIndex++
        loadInterstitialAd(adRequest, placement)
    }
}