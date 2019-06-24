package com.iamoem.repreference.core

import android.content.Context
import com.iamoem.repreference.db.DbHelper
import com.iamoem.repreference.rx.RxPref
import io.reactivex.Completable
import io.reactivex.Flowable


class RePreference(context: Context, name: String) {

    private val rxPref = RxPref()
    private val db: DbHelper = DbHelper(context.applicationContext, name)

    fun getAll(): Map<String, *> {
        db.getAll().apply {
            val result = HashMap<String, Any>()

            forEach {
                val (strValue, type) = it.value
                val value = createValueByType(strValue, type)
                result[it.key] = value
            }

            return result
        }
    }

    private fun createValueByType(strValue: String, type: DbHelper.TypeInfo): Any {
        return when(type) {
            DbHelper.TypeInfo.BOOL -> strValue.toBoolean()
            DbHelper.TypeInfo.FLOAT -> strValue.toFloat()
            DbHelper.TypeInfo.LONG -> strValue.toLong()
            DbHelper.TypeInfo.STRING -> strValue
            DbHelper.TypeInfo.INT -> strValue.toInt()
            DbHelper.TypeInfo.STRING_SET -> stringToStringSet(strValue) ?: emptySet<String>()
        }
    }

    fun contains(key: String): Boolean {
        return db.contains(key)
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return db.getValue(key, DbHelper.TypeInfo.BOOL)?.toBoolean() ?: defValue
    }

    fun getFloat(key: String, defValue: Float): Float {
        return db.getValue(key, DbHelper.TypeInfo.FLOAT)?.toFloat() ?: defValue
    }

    fun getInt(key: String, defValue: Int): Int {
        return db.getValue(key, DbHelper.TypeInfo.INT)?.toInt() ?: defValue
    }

    fun getLong(key: String, defValue: Long): Long {
        return db.getValue(key, DbHelper.TypeInfo.LONG)?.toLong() ?: defValue
    }

    fun getString(key: String, defValue: String): String {
        return db.getValue(key, DbHelper.TypeInfo.STRING) ?: defValue
    }

    fun getStringSet(key: String, defValue: Set<String>): Set<String> {
        return stringToStringSet(db.getValue(key, DbHelper.TypeInfo.STRING_SET)) ?: defValue
    }

    fun observeBoolean(key: String, defValue: Boolean): Flowable<Boolean> {
        return rxPref.createFlowable(key) {
            db.getValue(key, DbHelper.TypeInfo.BOOL)?.toBoolean() ?: defValue
        }
    }

    fun observeFloat(key: String, defValue: Float): Flowable<Float> {
        return rxPref.createFlowable(key) {
            db.getValue(key, DbHelper.TypeInfo.FLOAT)?.toFloat() ?: defValue
        }
    }

    fun observeInt(key: String, defValue: Int): Flowable<Int> {
        return rxPref.createFlowable(key) {
            db.getValue(key, DbHelper.TypeInfo.INT)?.toInt() ?: defValue
        }
    }

    fun observeLong(key: String, defValue: Long): Flowable<Long> {
        return rxPref.createFlowable(key) {
            db.getValue(key, DbHelper.TypeInfo.LONG)?.toLong() ?: defValue
        }
    }

    fun observeString(key: String, defValue: String): Flowable<String> {
        return rxPref.createFlowable(key) {
            db.getValue(key, DbHelper.TypeInfo.STRING) ?: defValue
        }
    }

    fun observeStringSet(key: String, defValue: Set<String>): Flowable<Set<String>> {
        return rxPref.createFlowable(key) {
            stringToStringSet(db.getValue(key, DbHelper.TypeInfo.STRING_SET)) ?: defValue
        }
    }

    private fun stringToStringSet(strVal: String?) : Set<String>? {
        return strVal?.trim('[', ']')
            ?.split(',')
            ?.toSet()
    }

    fun edit(): Editor {
        return EditorImpl(db, rxPref)
    }


    interface Editor {
        fun putString(key: String, value: String): Editor
        fun putStringSet(key: String, values: Set<String>): Editor
        fun putInt(key: String, value: Int): Editor
        fun putLong(key: String, value: Long): Editor
        fun putFloat(key: String, value: Float): Editor
        fun putBoolean(key: String, value: Boolean): Editor
        fun remove(key: String): Editor
        fun clear(): Editor
        fun commit(): Boolean
        fun apply(): Completable
    }
}


fun Context.getRePreferences(name: String) : RePreference {
    return RePreference(this, name)
}