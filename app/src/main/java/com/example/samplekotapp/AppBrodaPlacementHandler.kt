package com.example.samplekotapp

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object AppBrodaPlacementHandler {
    fun initRemoteConfigAndSavePlacements() {
        val mFirebaseRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        mFirebaseRemoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        })
        mFirebaseRemoteConfig.fetchAndActivate()
    }

    fun fetchAndSavePlacements() {
        Firebase.remoteConfig.fetchAndActivate()
    }

    fun loadPlacements(key: String): Array<String> {
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