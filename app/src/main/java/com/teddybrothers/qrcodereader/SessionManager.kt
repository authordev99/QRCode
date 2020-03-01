package com.teddybrothers.qrcodereader


import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class SessionManager(internal var context: Context) {

    companion object {
        const val PRIVATE_MODE = 0

        const val KEY_RESULT_SCAN_LIST = "result_scan_list"
        const val PREF_NAME = "qrcode"
    }

    private var editor: Editor
    private var pref: SharedPreferences

    val resultScanList: List<ResultScan>
        get() = Gson().fromJson<List<ResultScan>>(pref.getString(KEY_RESULT_SCAN_LIST, Gson().toJson(ArrayList<Any>())), object : TypeToken<List<ResultScan>>() {
        }.type)


    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun saveResultScanList(resultScanList: List<ResultScan>) {
        saveObject(KEY_RESULT_SCAN_LIST, resultScanList)
        editor.commit()
    }


    fun saveObject(KEY: String, value: Any) {
        editor.putString(KEY, Gson().toJson(value))
        editor.commit()
    }

    private fun clearSession() {
        editor.clear() // Clearing all data from Shared Preferences
        editor.commit()
    }



    fun getObject(KEY: String, clazz: Class<*>): Any {
        return Gson().fromJson(pref.getString(KEY, null), clazz)
    }

    fun getObject(KEY: String, type: Type): Any? {
        return Gson().fromJson<Any>(pref.getString(KEY, null), type)
    }
}