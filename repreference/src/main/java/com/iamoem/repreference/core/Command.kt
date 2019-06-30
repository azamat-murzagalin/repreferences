package com.iamoem.repreference.core

import com.iamoem.repreference.db.DbHelper


sealed class Command {
    class Clear()  : Command()
    class Put(val key: String,
              val value: String,
              val valType: DbHelper.TypeInfo) : Command()

    class Remove(val key: String) : Command()
}