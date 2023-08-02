package com.example.samplekotapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.samplekotapp.databinding.AdUnifiedBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class PageWithNativeAds : AppCompatActivity() {

    private lateinit var viewAds: FrameLayout
    private var nativeAdUnits = AppBrodaPlacementHandler.loadPlacements("com_example_samplekotapp_nativeAds")
    private var nativeIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_native_ads)
        viewAds = findViewById(R.id.view_ads)
        loadNativeAd(nativeAdUnits)
    }

    private fun loadNativeAd(adUnits:Array<String>) {
        val adUnitId = adUnits[nativeIndex]

        val adLoader = AdLoader.Builder(this, adUnitId)
            .forNativeAd { nativeAd: NativeAd ->
                viewAds.visibility = View.VISIBLE
                val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val viewUnifiedBinding = AdUnifiedBinding.inflate(layoutInflater)
                populateNativeAdView(nativeAd, viewUnifiedBinding)
                viewAds.removeAllViews()
                viewAds.addView(viewUnifiedBinding.root)
                // Ad loaded successfully, no need to load another ad unit.
                Toast.makeText(this, "Native ad loaded @index :$nativeIndex", Toast.LENGTH_SHORT).show()
                if (isDestroyed) {
                    nativeAd.destroy()
                    return@forNativeAd
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    // Ad failed to load, try loading the next ad unit.
                    Toast.makeText(this@PageWithNativeAds, "Native ad loading failed @index :$nativeIndex", Toast.LENGTH_SHORT).show()
                    loadNextAd()
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, unifiedBinding: AdUnifiedBinding) {
        val nativeAdView = unifiedBinding.root
        nativeAdView.mediaView = unifiedBinding.adMedia
        nativeAdView.headlineView = unifiedBinding.adHeadline
        nativeAdView.bodyView = unifiedBinding.adBody
        nativeAdView.callToActionView = unifiedBinding.adCallToAction
        nativeAdView.iconView = unifiedBinding.adAppIcon
        nativeAdView.priceView = unifiedBinding.adPrice
        nativeAdView.starRatingView = unifiedBinding.adStars
        nativeAdView.storeView = unifiedBinding.adStore
        nativeAdView.advertiserView = unifiedBinding.adAdvertiser
        unifiedBinding.adHeadline.text = nativeAd.headline
        nativeAd.mediaContent?.let {
            unifiedBinding.adMedia.mediaContent = it
        }

        if (nativeAd.body == null) {
            unifiedBinding.adBody.visibility = View.INVISIBLE
        } else {
            unifiedBinding.adBody.visibility = View.VISIBLE
            unifiedBinding.adBody.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            unifiedBinding.adCallToAction.visibility = View.INVISIBLE
        } else {
            unifiedBinding.adCallToAction.visibility = View.VISIBLE
            unifiedBinding.adCallToAction.text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            unifiedBinding.adAppIcon.visibility = View.INVISIBLE
        } else {
            unifiedBinding.adAppIcon.visibility = View.VISIBLE
            unifiedBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
        }

        if (nativeAd.price == null) {
            unifiedBinding.adPrice.visibility = View.INVISIBLE
        } else {
            unifiedBinding.adPrice.visibility = View.VISIBLE
            unifiedBinding.adPrice.text = nativeAd.price
        }

        if (nativeAd.store == null) {
            unifiedBinding.adStore.visibility = View.INVISIBLE
        } else {
            unifiedBinding.adStore.visibility = View.VISIBLE
            unifiedBinding.adStore.text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            unifiedBinding.adStars.visibility = View.INVISIBLE
        } else {
            unifiedBinding.adStars.visibility = View.VISIBLE
            unifiedBinding.adStars.rating = nativeAd.starRating!!.toFloat()
        }


        if (nativeAd.advertiser == null) {
            unifiedBinding.adAdvertiser.visibility = View.INVISIBLE
        } else {
            unifiedBinding.adAdvertiser.visibility = View.VISIBLE
            unifiedBinding.adAdvertiser.text = nativeAd.advertiser

        }
        nativeAdView.setNativeAd(nativeAd)

    }

    private fun loadNextAd() { //triggers next ad load
        if (nativeIndex == nativeAdUnits.size) {
            nativeIndex = 0
            return
        }
        nativeIndex++
        loadNativeAd(nativeAdUnits)
    }
}