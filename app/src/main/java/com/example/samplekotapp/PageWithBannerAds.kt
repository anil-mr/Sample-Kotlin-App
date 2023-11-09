package com.example.samplekotapp

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError


const val REFRESH_RATE = 30  //change refresh rate based on your ad unit id refresh rates
const val DURATION_BEFORE_REFRESH = 2000  // how long the ad has to be displayed just in time for a refresh
const val ONE_SECOND = 1000  // 1000ms = 1s
const val MAX_SWAP_DURATION = ((ONE_SECOND * REFRESH_RATE) - DURATION_BEFORE_REFRESH).toLong()
const val DURATION_OF_REFRESH_ATTEMPT = (ONE_SECOND * 57).toLong() // 3 seconds to make sure the adView is on screen before refresh attempt
const val MAX_SWAP_COUNT = (0.5 * REFRESH_RATE).toInt()   // after certain no of iterations we need to restart the entire flow again

const val TAG = "bannerAds"
class PageWithBannerAds : AppCompatActivity() {
    private var bannerIndex = 0
    private var adUnit = AppBrodaAdUnitHandler.loadAdUnit("com_example_samplekotapp_bannerAds")
    private val adViews: HashMap<Int, AdView> = HashMap()
    private var isFirstImpression:Boolean = true
    private val activeTimers: MutableList<CountDownTimer> = mutableListOf()
    private var swapCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_banner_ads)
        loadBannerAd(adUnit)
    }

    private fun loadBannerAd(adUnit: Array<String>?) {
        if (adUnit.isNullOrEmpty() || bannerIndex >= adUnit.size) return  //wrapper logic to handle errors
        val tempIndex = bannerIndex // because the bannerIndex might be outdated

        if(adViews[tempIndex] != null) { //if adView is present for this index, then do not load again
            loadNextAd()
            return
        }

        val adview = AdView(this)
        val adUnitId = adUnit[bannerIndex]
        adview.adUnitId = adUnitId
        adview.setAdSize(AdSize.BANNER)
        adview.loadAd(AdRequest.Builder().build())
        adview.adListener = object : AdListener() {

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Toast.makeText(
                    this@PageWithBannerAds,
                    "Banner ad loading failed @index :$tempIndex",
                    Toast.LENGTH_SHORT
                ).show()
                // we only need two adViews, with just these we can alternate b/w
                if(adViews.size < 2 || adViews[tempIndex] == null){
                    loadNextAd()
                }
                // start a timer again only if the refresh has failed and not on the first load of this unit id
                if(adViews[tempIndex] != null){
                    // if a refresh fails the sdk tries again in ~60 seconds
                    startTimer(DURATION_OF_REFRESH_ATTEMPT, {
                        displayAd(tempIndex)
                    })
                }
            }

            override fun onAdLoaded() {
                if(adViews[tempIndex] == null) {
                    adViews[tempIndex] = adview
                    displayAd(tempIndex)
                }
                Toast.makeText(
                    this@PageWithBannerAds,
                    "Banner ad loaded @index :$tempIndex",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdImpression() {
                Toast.makeText(
                    this@PageWithBannerAds,
                    "Impressed @index :$tempIndex",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i(TAG,"Impression received at index $tempIndex")
                super.onAdImpression()
                if(isFirstImpression){
                    // here ~700 is average take it takes for an ad to load, we need both the ads to be displayed for equal amount
                    // this will not guarantee exact equal watch time, but this will not create a huge difference b/w watch time of ad1 and ad2
                    val swapTimerDuration = ((MAX_SWAP_DURATION * 0.5) - (adUnit.size - (tempIndex+1)) * 700).toLong()
                    startTimer(swapTimerDuration, {
                        loadNextAd()
                    }, null)
                    isFirstImpression = false
                }

                startTimer(MAX_SWAP_DURATION, {
                    displayAd(tempIndex)
                })
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

    private fun startTimer(duration:Long, onFinish: () -> Unit, onTick: ((Long) -> Unit)? = null) {
        val timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onTick?.invoke(millisUntilFinished)
            }

            override fun onFinish() {
                onFinish()
                activeTimers.remove(this)
            }
        }.start()
        activeTimers.add(timer)
    }

    private fun displayAd(index:Int){
        if(swapCount >= MAX_SWAP_COUNT){
            swapCount = 0
            restartAdFlow()
            return
        }

        val adContainer = findViewById<RelativeLayout>(R.id.bannerAdView)
        val adView = adViews[index]
        adContainer.removeAllViews()
        if(adView != null) adContainer.addView(adView)
        swapCount++
    }

    private fun cancelAllActiveTimers() {
        for (timer in activeTimers) {
            timer.cancel()
        }
        activeTimers.clear()
    }

    private fun restartAdFlow(){
        bannerIndex = 0
        isFirstImpression = true
        cancelAllActiveTimers()
        for(adView in adViews.values){
            adView.destroy()
        }
        adViews.clear()
        findViewById<RelativeLayout>(R.id.bannerAdView).removeAllViews()
        loadBannerAd(adUnit)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAllActiveTimers()
    }
}