package com.example.samplekotapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appbrodasampleapp.AppBrodaAdUnitHandler
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class PageWithInterstitialAds : AppCompatActivity() {
    var mInterstitialAd: InterstitialAd? = null
    //private var adUnit: Array<String> = AppBrodaAdUnitHandler.loadAdUnit("com_example_samplekotapp_interstitialAds") as Array<String>
    var interstitialIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_interstitial_ads)
        //val adRequest = AdRequest.Builder().build()
        //loadInterstitialAd(adRequest, adUnit)
        val showAdButton = findViewById<Button>(R.id.showInterstitialAd)
        showAdButton.setOnClickListener {
            AppBrodaAdUnitHandler.showAd(this,"com_example_appbrodasampleapp_interstitialAds")
            //if (mInterstitialAd != null) {
            //    mInterstitialAd!!.show(this@PageWithInterstitialAds)
            //}
        }
        //AppBrodaAdUnitHandler.showQueue("com_example_samplekotapp_interstitialAds");
        AppBrodaAdUnitHandler.showAllQueue()
    }

    private fun loadInterstitialAd(adRequest: AdRequest?, adUnit: Array<String>) {
        if (adUnit.isEmpty() || interstitialIndex >= adUnit.size) //wrapper logic to handle errors
            return
        InterstitialAd.load(this, adUnit[interstitialIndex], adRequest!!,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Toast.makeText(
                        this@PageWithInterstitialAds,
                        "Interstitial Ad Loaded @index: $interstitialIndex", Toast.LENGTH_SHORT
                    ).show()
                    // Reset interstitialIndex to 0
                    interstitialIndex = 0
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Toast.makeText(
                        this@PageWithInterstitialAds,
                        "Interstitial Ad Loading failed @index: $interstitialIndex",
                        Toast.LENGTH_SHORT
                    ).show()
                    mInterstitialAd = null
                    loadNextAd(adRequest, adUnit)
                }
            })
    }

    fun loadNextAd(adRequest: AdRequest?, adUnit: Array<String>) {
        interstitialIndex++
        if (interstitialIndex >= adUnit.size) {
            interstitialIndex = 0
            return
        }
        loadInterstitialAd(adRequest, adUnit)
    }
}