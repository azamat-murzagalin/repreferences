package com.iamoem.repreference.db

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.iamoem.repreference.db.DbContract.Value.FIELD_KEY
import com.iamoem.repreference.db.DbContract.Value.FIELD_TYPE
import com.iamoem.repreference.db.DbContract.Value.FIELD_VALUE
import com.iamoem.repreference.db.DbContract.Value.TABLE_NAME

class DbHelper(context: Context, dbName: String) : SQLiteOpenHelper(context, dbName, null, DATABASE_VERSION) {

    private val db: SQLiteDatabase by lazy { writableDatabase }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DbContract.Value.SQL_CREATE_VALUES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    companion object {
        const val DATABASE_VERSION = 1
    }

    /**
     * Returns all items in map value to type information
     * @return Map Key -> Value and Type information
     */
    fun getAll() : Map<String, Pair<String, TypeInfo>> {
        var cursor : Cursor? = null

        try {
            cursor = selectAll()
            val result = HashMap<String, Pair<String, TypeInfo>>()

            cursor?.let { c ->
                while (c.moveToNext()) {
                    val valIndex = c.getColumnIndex(FIELD_VALUE)
                    val typeIndex = c.getColumnIndex(FIELD_TYPE)
                    val keyIndex = c.getColumnIndex(FIELD_KEY)

                    val value = c.getString(valIndex)
                    val typeStr = c.getString(typeIndex)
                    val key = c.getString(keyIndex)
                    result[key] = value to TypeInfo.valueOf(typeStr)
                }
            }

            return result
        } finally {
            cursor?.close()
        }
    }

    /**
     * Checks if a row with the key exists in database
     * @param key to check the database
     */
    fun contains(key: String) : Boolean {
        var cursor : Cursor? = null

        try {
            cursor = getCursorByKey(key)

            return (cursor?.count ?: 0) > 0
        } finally {
            cursor?.close()
        }
    }

    /**
     * Begins a transaction in EXCLUSIVE mode.
     */
    fun beginTransaction() {
        db.beginTransaction()
    }


    /**
     * Ends a transaction.
     */
    fun endTransaction() {
        db.endTransaction()
    }

    /**
     * Marks the current transaction as successful. Do not do any more database work between
     * calling this and calling endTransaction. Do as little non-database work as possible in that
     * situation too. If any errors are encountered between this and endTransaction the transaction
     * will still be committed.
     *
     * @throws IllegalStateException if the current thread is not in a transaction or the
     * transaction is already marked as successful.
     */
    fun setTransactionSuccessful() {
        db.setTransactionSuccessful()
    }

    /**
     * Adds key value pair to database
     * @param key key
     * @param value string value
     * @param type string value of type
     * @throws SQLException in case of violations errors
     */
    fun addValue(key: String, value: String, type: TypeInfo) {
        db.execSQL(DbContract.Value.createInsertRequest(key, value, type.name))
    }

    /**
     * Removes key value pair from database
     * @param key key
     * @throws SQLException in case of violations errors
     */
    fun removeValue(key: String) {
        db.execSQL(DbContract.Value.createRemoveRequest(key))
    }

    /**
     * Clears database
     * @throws SQLException in case of violations errors
     */
    fun clear() {
        db.execSQL(DbContract.Value.createClearRequest())
    }

    /**
     * Returns value by key from database
     * @param key key
     * @return value or <code>null</code> if there is no value
     */
    @Suppress("LiftReturnOrAssignment") // it is suppressed because it makes no sense
    fun getValue(key: String, type: TypeInfo): String? {
        var c: Cursor? = null
        try {
            c = getCursorByKey(key)
            if (c.moveToFirst()) {
                val valIndex = c.getColumnIndex(FIELD_VALUE)
                val typeIndex = c.getColumnIndex(FIELD_TYPE)
                val value = c.getString(valIndex)
                val typeStr = c.getString(typeIndex)
                if (type != TypeInfo.valueOf(typeStr)) throw IllegalArgumentException(
                    "key:'$key' was saved with type:'$typeStr', but you tried to access it with type:'${type}'"
                )
                return value
            } else {
                return null
            }
        } finally {
            c?.close()
        }
    }


    private fun getCursorByKey(key: String) = db.query(
        TABLE_NAME,
        null,
        "$FIELD_KEY = ?",
        arrayOf(key),
        null,
        null,
        null
    )

    private fun selectAll() = db.query(
        TABLE_NAME,
        null,
        null,
        null,
        null,
        null,
        null
    )

    enum class TypeInfo {
        STRING,
        INT,
        LONG,
        BOOL,
        FLOAT,
        STRING_SET
    }
}
