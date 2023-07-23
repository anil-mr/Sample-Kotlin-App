package com.example.samplekotapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds

const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var testButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {}
        AppBrodaPlacementHandler.initRemoteConfigAndSavePlacements(this@MainActivity)

        testButton = findViewById(R.id.testBtn)
        var counter = 0
        testButton.setOnClickListener{
            counter++
            Toast.makeText(this, "You clicked $counter times", Toast.LENGTH_SHORT).show()
        }

    }

    fun bannerPage(v: View?) {
        val i = Intent(this, PageWithBannerAds::class.java)
        startActivity(i)
    }

    fun interstitialPage(v: View?) {
        val i = Intent(this, PageWithInterstitialAds::class.java)
        startActivity(i)
    }

    fun rewardedPage(v: View?) {
        val i = Intent(this, PageWithRewardedAds::class.java)
        startActivity(i)
    }

    fun nativePage(v: View?) {
        val i = Intent(this, PageWithNativeAds::class.java)
        startActivity(i)
    }
}