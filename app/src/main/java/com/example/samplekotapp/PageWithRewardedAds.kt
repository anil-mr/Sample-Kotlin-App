package com.example.samplekotapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appbrodasampleapp.AppBrodaAdUnitHandler
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class PageWithRewardedAds : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_rewarded_ads)

        val showAdButton = findViewById<Button>(R.id.showAds)
        showAdButton.setOnClickListener {
            AppBrodaAdUnitHandler.showAd(this,"com_example_appbrodasampleapp_rewardedAds")
        }
    }
}