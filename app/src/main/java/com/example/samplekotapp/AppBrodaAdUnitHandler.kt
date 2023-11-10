package com.example.appbrodasampleapp

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.ads.android.adscache.AdsCache
import com.google.ads.android.adscache.queue.AdsQueueCallback
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Error
import java.util.SortedMap
import java.util.TreeMap

object AppBrodaAdUnitHandler {
    var adsCache: AdsCache? = null
    var unitIdToAdUnitMap: HashMap<String?, String> = HashMap()
    var unitIdToIndexMap: HashMap<String?, Int> = HashMap()
    var CacheMap: HashMap<String?, SortedMap<Int, String>> = HashMap()

    var adsConfigArray = JSONArray()
    fun initRemoteConfigAndSaveAdUnits(ApplicationContext: Context?) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        // set the minimum fetch interval to 0 during testing
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
        initAdsCache(ApplicationContext)
    }

    fun fetchAndSaveAdUnits(ApplicationContext: Context?) {
        FirebaseRemoteConfig.getInstance().fetchAndActivate()
        initAdsCache(ApplicationContext)
    }

    fun initAdsCache(ApplicationContext: Context?) {
        var firebaseInstance = FirebaseRemoteConfig.getInstance()
        val adUnitsConfigInterstitialQueueSize = firebaseInstance.getDouble("AdsConfig_Interstitial_QueueSize").takeIf { it != 0.0 } ?: 1
        val adUnitsConfigInterstitialLoadInterval = firebaseInstance.getDouble("AdsConfig_Interstitial_LoadInterval").takeIf { it != 0.0 } ?: 1800

        val adUnitsConfigRewardedQueueSize = firebaseInstance.getDouble("AdsConfig_Rewarded_QueueSize").takeIf { it != 0.0 } ?: 1
        val adUnitsConfigRewardedLoadInterval = firebaseInstance.getDouble("AdsConfig_Rewarded_LoadInterval").takeIf { it != 0.0 } ?: 3600

        /* Define your ad units here */
        pushAdUnitToConfig(
            "com_example_appbrodasampleapp_interstitialAds",
            "INTERSTITIAL",
            adUnitsConfigInterstitialQueueSize,
            adUnitsConfigInterstitialLoadInterval
        )
        pushAdUnitToConfig(
            "com_example_appbrodasampleapp_rewardedAds",
            "REWARDED",
            adUnitsConfigRewardedQueueSize,
            adUnitsConfigRewardedLoadInterval
        )
        /* ------ */

        //Log.d("adsConfig", "full config "+myArray.toString()) //?
        var adsConfig = JSONArray()
        try {
            adsConfig = JSONArray(adsConfigArray.toString())
        } catch (e: JSONException) {
            Log.d("[App]", "Unable to parse the Ads Config $e")
        }

        adsCache =
            AdsCache(ApplicationContext, adsConfig, object : AdsQueueCallback {
                override fun onAdsAvailable(ad_unit_id: String) {
                    Log.d("OptiCache", "New Ad is available for $ad_unit_id")
                    setUnitIdPriority(ad_unit_id)
                }

                override fun onAdsExhausted(ad_unit_id: String) {}
            })
        adsCache!!.initialize()
    }

    fun loadAdUnit(key: String?): Array<String?> {
        val value = FirebaseRemoteConfig.getInstance().getString(key!!)
        return if (value.isEmpty()) arrayOfNulls(0) else convertToArray(value)
    }

    private fun convertToArray(value: String): Array<String?> {
        var value: String? = value
        var array = arrayOfNulls<String>(0)
        if (value!!.isEmpty() || value == null) return array
        value = value.substring(1, value.length - 1)
        array = value.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in array.indices) {
            array[i] = array[i]!!.trim { it <= ' ' }.replace("^\"|\"$".toRegex(), "")
        }
        return array
    }

    fun showAd(unitId: String?, activity: Activity?) {
        adsCache!!.showAd(unitId, activity)
    }

    fun showAd(activity: Activity?, adUnitId: String?) {
        val priorityMap = CacheMap[adUnitId]
        Log.d("OptiCache", "CacheMap $adUnitId = $priorityMap")
        val firstKey: Int? = priorityMap?.keys?.firstOrNull()

        if (firstKey == null) {
            Log.d("OptiCache","Empty Cache.")
            return;
        }

        val unitId = priorityMap[firstKey]
        priorityMap.remove(firstKey)
        Log.d("OptiCache", "Showing ad: $unitId")
        Log.d("OptiCache", "Popped : $unitId")
        adsCache!!.showAd(unitId, activity)
    }

    fun storeUnitIdToAdUnitMap(unitIds: Array<String?>, adUnitId: String) {
        try {
            for (i in unitIds.indices) {
                unitIdToAdUnitMap[unitIds[i]] = adUnitId
                unitIdToIndexMap[unitIds[i]] = i
            }
            val priorityMap: SortedMap<Int, String> = TreeMap()
            CacheMap[adUnitId] = priorityMap
        } catch (e:Error){
            Log.e("OptiCache","Error creating custom maps")
        }
    }

    fun setUnitIdPriority(unitId: String) {
        val adUnit = unitIdToAdUnitMap[unitId]
        if(adUnit == null){
            Log.d("OptiCache", "UnitId $unitId does not exists in map")
            return;
        }

        val optiCache = CacheMap[adUnit]
        if (optiCache != null) {
            val index = unitIdToIndexMap[unitId]!!
            optiCache[index] = unitId
            CacheMap[adUnit] = optiCache
            showQueue(adUnit);
        }
    }

    fun showQueue(adUnit: String?) {
        val optiCache = CacheMap[adUnit]!!
        Log.d("OptiCache", "Updated OptiCache - $adUnit")
        for (key in optiCache.keys) {
            Log.d("OptiCache", " UnitId: " + optiCache[key])
        }
    }

    fun showAllQueue() {
        Log.d("OptiCache", "Showing all Maps")
        for (cacheMapKey in CacheMap.keys) {
            Log.d("OptiCache", "Showing for $cacheMapKey")
            for (key in CacheMap[cacheMapKey]!!.keys) {
                Log.d("OptiCache", "AdUnit: $key - Map: " + CacheMap[cacheMapKey]!![key])
            }
        }
    }

    fun pushAdUnitToConfig(adUnitId: String, format: String?, queueSize: Number, loadInterval: Number) {
        val jsonArray = adsConfigArray
        val unitIds = loadAdUnit(adUnitId)
        for (i in unitIds.indices) {
            try {
                val newElement = JSONObject()
                newElement.put("adUnitId", unitIds[i])
                newElement.put("format", format)
                newElement.put("queueSize", queueSize)
                newElement.put("loadInterval", loadInterval)
                jsonArray.put(newElement)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        storeUnitIdToAdUnitMap(unitIds, adUnitId)
    }
}