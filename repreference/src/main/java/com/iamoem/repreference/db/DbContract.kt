package com.iamoem.repreference.db

import android.provider.BaseColumns

object DbContract {
    object Value : BaseColumns {
        const val TABLE_NAME = "T_VALUE"
        const val FIELD_KEY = "P_KEY"
        const val FIELD_VALUE = "P_VALUE"
        const val FIELD_TYPE = "P_TYPE"

        const val SQL_CREATE_VALUES =
            """CREATE TABLE $TABLE_NAME (
                    $FIELD_KEY TEXT NOT NULL PRIMARY KEY,
                    $FIELD_VALUE TEXT NOT NULL,
                    $FIELD_TYPE TEXT NOT NULL)"""

        const val SQL_DELETE_VALUES = "DROP TABLE IF EXISTS $TABLE_NAME"

        fun createInsertRequest(key: String, value: String, type: String) = """
            INSERT OR REPLACE INTO $TABLE_NAME ("$FIELD_KEY", "$FIELD_VALUE", "$FIELD_TYPE")
            VALUES ("$key", "$value", "$type")
        """

        fun createRemoveRequest(key: String) = """
            DELETE FROM $TABLE_NAME WHERE $FIELD_KEY = "$key"
        """

        fun createClearRequest() = """
            DELETE FROM $TABLE_NAME
        """
    }
}