package com.example.samplekotapp

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class PageWithBannerAds : AppCompatActivity() {
    var index = 0
    private var adUnits = AppBrodaPlacementHandler.loadPlacements("com_example_samplekotapp_bannerAds")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("PageWithBannerAds", "Adunits ${adUnits.size}");
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_banner_ads)
        loadBannerAd(adUnits)

    }

    private fun loadBannerAd(adUnits: Array<String>?) {
        if (adUnits.isNullOrEmpty()) return  //wrapper logic to handle errors
        val adview = AdView(this)
        val adUnitId = adUnits[index]
        adview.adUnitId = adUnitId
        adview.setAdSize(AdSize.BANNER)
        adview.loadAd(
            AdRequest.Builder()
                .build()
        )
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layout.addView(adview, params)
        setContentView(layout)
        adview.adListener = object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Toast.makeText(
                    this@PageWithBannerAds,
                    "Banner ad loading failed @index :$index",
                    Toast.LENGTH_SHORT
                ).show()
                loadNextAd() //call to triggers next load
            }

            override fun onAdLoaded() {
                Toast.makeText(
                    this@PageWithBannerAds,
                    "Banner ad loaded @index :$index",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadNextAd() { //triggers next ad load
        if (index == adUnits.size) {
            index = 0
            return
        }
        index++
        loadBannerAd(adUnits)
    }

}