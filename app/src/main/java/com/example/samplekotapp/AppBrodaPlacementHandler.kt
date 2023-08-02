package com.example.samplekotapp

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object AppBrodaPlacementHandler {
    @SuppressLint("StaticFieldLeak")
    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null;

    fun initRemoteConfigAndSavePlacements(context: Context) {
        val mFirebaseRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        mFirebaseRemoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        })
        setFirebaseRemoteConfig(mFirebaseRemoteConfig)
        mFirebaseRemoteConfig.fetchAndActivate()
    }

    fun fetchAndSavePlacements(mFirebaseRemoteConfig: FirebaseRemoteConfig, context: Context) {
        mFirebaseRemoteConfig.fetchAndActivate()
      setFirebaseRemoteConfig(mFirebaseRemoteConfig)
    }

    fun loadPlacements(key: String): Array<String> {
        val value = firebaseRemoteConfig?.getString(key);
        if (value === "") {
            return arrayOf()
        }
        val placement = convertToArray(value)
        return placement ?: arrayOf()
    }

    private fun convertToArray(value: String?): Array<String> {
        val newValue = value!!.substring(1, value.length - 1)
        val array: Array<String> = newValue.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in array.indices) {
            array[i] = array[i].trim { it <= ' ' }.replace("^\"|\"$".toRegex(), "")
        }
        return array
    }

    private fun setFirebaseRemoteConfig (remoteConfig:FirebaseRemoteConfig){
        firebaseRemoteConfig = remoteConfig
    }
}