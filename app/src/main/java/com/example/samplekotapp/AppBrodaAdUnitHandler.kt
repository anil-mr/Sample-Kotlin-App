package com.example.samplekotapp

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object AppBrodaAdUnitHandler {
    fun initRemoteConfigAndSaveAdUnits() {
        val mFirebaseRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        mFirebaseRemoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 900
        })
        // set the minimum fetch interval to 0 during testing
        mFirebaseRemoteConfig.fetchAndActivate()
    }

    fun fetchAndSaveAdUnits() {
        Firebase.remoteConfig.fetchAndActivate()
    }

    fun loadAdUnit(key: String): Array<String> {
        val value = FirebaseRemoteConfig.getInstance().getString(key)
        if (value.isEmpty()) return arrayOf()
        return convertToArray(value)
    }

    private fun convertToArray(value: String?): Array<String> {
        if(value.isNullOrEmpty()) return arrayOf()
        val array: Array<String> = value.substring(1, value.length - 1).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in array.indices) {
            array[i] = array[i].trim { it <= ' ' }.replace("^\"|\"$".toRegex(), "")
        }
        return array
    }

}