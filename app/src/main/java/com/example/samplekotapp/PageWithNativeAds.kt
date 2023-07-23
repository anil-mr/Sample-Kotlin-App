package com.example.samplekotapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.example.samplekotapp.databinding.AdUnifiedBinding

class PageWithNativeAds : AppCompatActivity() {

    private lateinit var viewAds: FrameLayout
    private var adUnits = AppBrodaPlacementHandler.loadPlacements("nativeAds")
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_with_native_ads)
        viewAds = findViewById(R.id.view_ads)
        loadNativeAd()
    }

    private fun loadNativeAd() {
        if (index >= adUnits.size) {
            return
        }

        val adUnitId = adUnits[index]

        val adLoader = AdLoader.Builder(this, adUnitId)
            .forNativeAd { nativeAd: NativeAd ->
                viewAds.visibility = View.VISIBLE
                val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val viewUnifiedBinding = AdUnifiedBinding.inflate(layoutInflater)
                populateNativeAdView(nativeAd, viewUnifiedBinding)
                viewAds.removeAllViews()
                viewAds.addView(viewUnifiedBinding.root)
                // Ad loaded successfully, no need to load another ad unit.
                Toast.makeText(this, "Native ad loaded @index :$index", Toast.LENGTH_SHORT).show()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    // Ad failed to load, try loading the next ad unit.
                    Toast.makeText(this@PageWithNativeAds, "Native ad loading failed @index :$index", Toast.LENGTH_SHORT).show()
                    index++
                    loadNativeAd()
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
            unifiedBinding.adMedia.setMediaContent(it)
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

        val vc = nativeAd.mediaContent?.videoController

        if (vc != null && vc.hasVideoContent()) {
            vc.videoLifecycleCallbacks =
                object : VideoController.VideoLifecycleCallbacks() {

                    override fun onVideoEnd() {

                        super.onVideoEnd()
                    }
                }
        } else {

        }


    }


}