package com.example.samplekotapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appbrodasampleapp.AppBrodaAdUnitHandler
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class PageWithRewardedAds : AppCompatActivity() {
    private var rewardedAd: RewardedAd? = null
    //private val adUnit: Array<String> = AppBrodaAdUnitHandler.loadAdUnit("com_example_samplekotapp_rewardedAds") as Array<String>
    private var rewardedIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_rewarded_ads)
        AppBrodaAdUnitHandler.showAd(this, "com_example_appbrodasampleapp_rewardedAds")
        //loadRewardedAd(adUnit)
    }

    private fun loadRewardedAd(adUnit: Array<String>) {
        if (adUnit.isEmpty() || rewardedIndex >= adUnit.size ) //wrapper logic to handle errors
            return
        RewardedAd.load(this, adUnit[rewardedIndex],
            AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    Toast.makeText(
                        this@PageWithRewardedAds,
                        "Rewarded Ad failed to load @index: $rewardedIndex", Toast.LENGTH_SHORT
                    ).show()
                    //loadNextAd()
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Toast.makeText(
                        this@PageWithRewardedAds,
                        "Rewarded Ad loaded @index: $rewardedIndex", Toast.LENGTH_SHORT
                    ).show()
                    // Reset rewardedIndex to 0
                    rewardedIndex = 0;
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

   /* private fun loadNextAd() { //triggers next ad load
        rewardedIndex++
        if (rewardedIndex >= //adUnit.size) {
            rewardedIndex = 0
            return
        }
        loadRewardedAd(adUnit)
    }*/
}