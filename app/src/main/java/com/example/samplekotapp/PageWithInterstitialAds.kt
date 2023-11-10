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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_interstitial_ads)
        val showAdButton = findViewById<Button>(R.id.showInterstitialAd)

        showAdButton.setOnClickListener {
            AppBrodaAdUnitHandler.showAd(this,"com_example_appbrodasampleapp_interstitialAds")
        }
    }
}