package com.example.samplekotapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class PageWithRewardedAds : AppCompatActivity() {
    private var rewardedAd: RewardedAd? = null
    private val placement: Array<String> = AppBrodaPlacementHandler.loadPlacements("rewardedAds")
    private var rewardedIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_rewarded_ads)
        loadRewardedAd(placement)
    }

    private fun loadRewardedAd(placement: Array<String>) {
        if (placement == null || placement.isEmpty()) //wrapper logic to handle errors
            return
        RewardedAd.load(this, placement[rewardedIndex]!!,
            AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    Toast.makeText(
                        this@PageWithRewardedAds,
                        "Rewared Ad failed to load @index: $rewardedIndex", Toast.LENGTH_SHORT
                    ).show()
                    loadNextAd()
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Toast.makeText(
                        this@PageWithRewardedAds,
                        "Rewared Ad loaded @index: $rewardedIndex", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun showAd(v: View?) {
        if (rewardedAd != null) {
            val activityContext: Activity = this@PageWithRewardedAds
            rewardedAd!!.show(
                activityContext
            ) { rewardItem -> // Handle the reward.
                Log.d("TAG", "The user earned the reward.")
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
            }
        } else {
            Log.d("TAG", "The rewarded ad wasn't ready yet.")
        }
    }

    private fun loadNextAd() { //triggers next ad load
        if (rewardedIndex == placement.size) {
            rewardedIndex = 0
            return
        }
        rewardedIndex++
        loadRewardedAd(placement)
    }
}