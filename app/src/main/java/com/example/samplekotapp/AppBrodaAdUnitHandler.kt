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

    //public static String[] unitIds = {"ca-app-pub-3940256099942544/1033173712","/6499/example/interstitial"};
    //public static String[] unitIdsRewarded = {"/6499/example/rewarded","ca-app-pub-3940256099942544/5224354917"};
    var adsConfigArray = JSONArray()

    var flip = true
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

    fun fetchAndSaveAdUnits() {
        FirebaseRemoteConfig.getInstance().fetchAndActivate()
    }

    fun initAdsCache(ApplicationContext: Context?) {
        var firebaseInstance = FirebaseRemoteConfig.getInstance()
        var adUnitsConfigInterstitialQueueSize = FirebaseRemoteConfig.getInstance().getDouble("AdsConfig_Interstitial_QueueSize").takeIf { it != 0.0 } ?: 1
        var adUnitsConfigInterstitialLoadInterval = FirebaseRemoteConfig.getInstance().getDouble("AdsConfig_Interstitial_LoadInterval").takeIf { it != 0.0 } ?: 1800

        var adUnitsConfigRewardedQueueSize = FirebaseRemoteConfig.getInstance().getDouble("AdsConfig_Rewarded_QueueSize").takeIf { it != 0.0 } ?: 1
        var adUnitsConfigRewardedLoadInterval = FirebaseRemoteConfig.getInstance().getDouble("AdsConfig_Rewarded_LoadInterval").takeIf { it != 0.0 } ?: 3600

        /* Define your ad units here */
        addAdUnitToJsonArray(
            "com_example_appbrodasampleapp_interstitialAds",
            "INTERSTITIAL",
            adUnitsConfigInterstitialQueueSize,
            adUnitsConfigInterstitialLoadInterval
        )
        addAdUnitToJsonArray(
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
        //Log.d("newAds", "com_example_samplekotapp_interstitialAds is $value ") //?
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
        //Log.d("newAds", "Inside showAd $adUnitId")
        //showAllQueue(); //?
        //Log.d("newAds", "The while Priority map is $adUnitIdtoPriorityMap")
        val priorityMap = CacheMap[adUnitId]
        Log.d("OptiCache", "CacheMap $adUnitId = $priorityMap")
        val firstKey: Int? = priorityMap?.keys?.firstOrNull()

        if (firstKey == null) {
            Log.d("OptiCache","Empty Cache.")
            return;
        }
        //Log.d("newAds", "First key is : $firstKey") /?
        val unitId = priorityMap[firstKey]
        priorityMap.remove(firstKey)
        Log.d("OptiCache", "Showing ad: $unitId")
        Log.d("OptiCache", "Popped : $unitId")
        adsCache!!.showAd(unitId, activity)
        //Log.d("newAds core", "After Remove") //?

        /*for (key in priorityMap.keys) { //?
            println("Key: " + key + ", Value: " + priorityMap[key])
            Log.d("newAds core", "Key: " + key + ", Value: " + priorityMap[key])
        }*/
    }

    fun storeUnitIdToAdUnitMap(unitIds: Array<String?>, adUnitId: String) {
        try {
            for (i in unitIds.indices) {
                unitIdToAdUnitMap[unitIds[i]] = adUnitId
                unitIdToIndexMap[unitIds[i]] = i
            }
            //Log.d("newAds", "Saving for unitId $adUnitId") /?
            val priorityMap: SortedMap<Int, String> = TreeMap()
            CacheMap[adUnitId] = priorityMap
            //Log.d("newAds", "After adding") ?
            /*for (key in unitIdToAdUnitMap.keys) {
                println("Key: " + key + ", Value: " + unitIdToAdUnitMap[key])
                Log.d("newAds", "Key: " + key + ", Value: " + unitIdToAdUnitMap[key])
            }*/
        } catch (e:Error){
            Log.e("OptiCache","Error creating custom maps")
        }
    }

    fun setUnitIdPriority(unitId: String) {
        val adUnit = unitIdToAdUnitMap[unitId]
        //Log.d("newAds", "AdUnitId from unitIdToAdUnitMao $adUnit")/?

        val priorityMap = CacheMap[adUnit]
        if (priorityMap != null) {
            //Log.d("newAds", "Adding new unit id$unitId")/?
            val index = unitIdToIndexMap[unitId]!!
            //Log.d("newAds", "Index is$index")?
            priorityMap[index] = unitId
            //val priorityMap = priorityMap.toSortedMap(compareBy { it })
            CacheMap[adUnit] = priorityMap
            //Log.d("newAds", "Priority map")/?
            /*for (key in priorityMap.keys) {
                println("Key: " + key + ", Value: " + priorityMap[key])
                Log.d("newAds core", "Key: " + key + ", Value: " + priorityMap[key])
            }*/

            //Log.d("newAds", "The Entire PriorityMapMap")/?
            /*for (key1 in adUnitIdtoPriorityMap.keys) {
                val map = adUnitIdtoPriorityMap[key1]!!
                println("Key: " + key1 + ", Value: " + adUnitIdtoPriorityMap[key1])
                Log.d("newAds", "AdUnitId: " + key1 + ", MAP: " + adUnitIdtoPriorityMap[key1])
                for (key2 in map.keys) {
                    println("Key: " + key2 + ", Value: " + map[key2])
                    Log.d("newAds", "Key: " + key2 + ", Value: " + map[key2])
                }

            }*/
            showQueue(adUnit);

        }
    }

    fun showQueue(adUnit: String?) {
        val priorityMap = CacheMap[adUnit]!!
        Log.d("OptiCache", "Updated OptiCache - $adUnit")
        for (key in priorityMap.keys) {
            Log.d("OptiCache", " UnitId: " + priorityMap[key])
        }
    }

    fun showAllQueue() {
        Log.d("newAds", "Priority map show all Queue")

        for (key1 in CacheMap.keys) {
            Log.d("newAds", "Showing for $key1")
            for (key in CacheMap[key1]!!.keys) {
                println(
                    "AdUnit: $key, Map: " + CacheMap[key1]!![key]
                )
                Log.d("newAds", "Key: $key, Value: " + CacheMap[key1]!![key])
            }
        }
    }

    fun addAdUnitToJsonArray(adUnitId: String, format: String?, queueSize: Number, loadInterval: Number) {
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
                Log.d("newAds", newElement["adUnitId"].toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        storeUnitIdToAdUnitMap(unitIds, adUnitId)
    }
}