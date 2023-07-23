package com.example.samplekotapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object AppBrodaPlacementHandler {
    private var sharedPreferences: SharedPreferences? = null
    @SuppressLint("StaticFieldLeak")
    private const val sharedPreferenceKey = "AppBroda_pref"
    private var abAppKey: String? = null
    fun initRemoteConfigAndSavePlacements(context: Context) {
        abAppKey = context.packageName.replace(".", "_") + "_"
        val mFirebaseRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        mFirebaseRemoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        })
        mFirebaseRemoteConfig?.fetchAndActivate()
            ?.addOnCompleteListener(OnCompleteListener<Boolean> { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                }
            })
        sharedPreferences = context.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val remoteConfigKeys: Set<String> = mFirebaseRemoteConfig!!.getKeysByPrefix(abAppKey!!)
        for (key in remoteConfigKeys) {
            val newKey = getKey(trimPrefix(key, abAppKey))
            val value: String = mFirebaseRemoteConfig!!.getString(key)
            editor?.putString(newKey, value)
        }
        editor?.apply()
    }

    fun loadPlacements(key: String): Array<String> {
        val newKey = getKey(key)
        val value = sharedPreferences!!.getString(newKey, "")
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

    private fun getKey(key: String): String {
        return abAppKey + key
    }

    private fun trimPrefix(key: String, abAppKey: String?): String {
        return (if (key.startsWith(abAppKey!!)) key.substring(abAppKey.length) else key).toString()
    }
}