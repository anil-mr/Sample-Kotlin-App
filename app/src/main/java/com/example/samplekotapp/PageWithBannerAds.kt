package com.example.samplekotapp

import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appbrodasampleapp.AppBrodaAdUnitHandler
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class PageWithBannerAds : AppCompatActivity() {
    var bannerIndex = 0
    private var adUnit = AppBrodaAdUnitHandler.loadAdUnit("com_example_samplekotapp_bannerAds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_banner_ads)
        loadBannerAd(adUnit)

    }

    private fun loadBannerAd(adUnit: Array<out String?>) {
        if (adUnit.isNullOrEmpty() || bannerIndex >= adUnit.size) return  //wrapper logic to handle errors
        val adview = AdView(this)
        val adContainer = findViewById<RelativeLayout>(R.id.bannerAdView)
        val adUnitId = adUnit[bannerIndex]
        if (adUnitId != null) {
            adview.adUnitId = adUnitId
        }
        adview.setAdSize(AdSize.BANNER)
        adview.loadAd(
            AdRequest.Builder()
                .build()
        )
        adContainer.addView(adview)
        adview.adListener = object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Toast.makeText(
                    this@PageWithBannerAds,
                    "Banner ad loading failed @index :$bannerIndex",
                    Toast.LENGTH_SHORT
                ).show()
                loadNextAd() //call to triggers next load
            }

            override fun onAdLoaded() {
                Toast.makeText(
                    this@PageWithBannerAds,
                    "Banner ad loaded @index :$bannerIndex",
                    Toast.LENGTH_SHORT
                ).show()
                // Reset bannerIndex to 0
                bannerIndex = 0
            }
        }
    }

    private fun loadNextAd() { //triggers next ad load
        bannerIndex++
        if (bannerIndex >= adUnit.size) {
            bannerIndex = 0
            return
        }
        loadBannerAd(adUnit)
    }

}