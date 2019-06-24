package com.iamoem.repreference.core

import com.iamoem.repreference.db.DbHelper
import com.iamoem.repreference.rx.RxPref
import io.reactivex.Completable


class EditorImpl(
    private val dbHelper: DbHelper,
    private val rxPref: RxPref
) : RePreference.Editor {

    private val commands: MutableList<Command> = ArrayList()

    override fun putString(key: String, value: String): RePreference.Editor {
        commands.add(Command.Put(key, value, DbHelper.TypeInfo.STRING))
        return this
    }

    override fun putStringSet(key: String, values: Set<String>): RePreference.Editor {
        commands.add(Command.Put(key, values.toString(), DbHelper.TypeInfo.STRING_SET))
        return this
    }

    override fun putInt(key: String, value: Int): RePreference.Editor {
        commands.add(Command.Put(key, value.toString(), DbHelper.TypeInfo.INT))
        return this
    }

    override fun putLong(key: String, value: Long): RePreference.Editor {
        commands.add(Command.Put(key, value.toString(), DbHelper.TypeInfo.LONG))
        return this
    }

    override fun putFloat(key: String, value: Float): RePreference.Editor {
        commands.add(Command.Put(key, value.toString(), DbHelper.TypeInfo.FLOAT))
        return this
    }

    override fun putBoolean(key: String, value: Boolean): RePreference.Editor {
        commands.add(Command.Put(key, value.toString(), DbHelper.TypeInfo.BOOL))
        return this
    }

    override fun remove(key: String): RePreference.Editor {
        commands.add(Command.Remove(key))
        return this
    }

    override fun clear(): RePreference.Editor {
        commands.add(Command.Clear())
        return this
    }

    override fun commit(): Boolean {
        var result = false
        dbHelper.beginTransaction()

        try {
            commands.forEach {
                processCommand(it)
            }

            dbHelper.setTransactionSuccessful()

            result = true

            notifyCommandsDone(commands)

            commands.clear()
        } finally {
            dbHelper.endTransaction()
        }

        return result
    }

    private fun notifyCommandsDone(commands: MutableList<Command>) {
        if (commands.any { it is Command.Clear }) {
            rxPref.notifyAllChanged()
        } else {
            commands.forEach { command ->
                when (command) {
                    is Command.Put -> {
                        rxPref.notifyValueChanged(command.key)
                    }
                    is Command.Remove -> {
                        rxPref.notifyValueChanged(command.key)
                    }
                }
            }
        }
    }

    private fun processCommand(command: Command) {
        when (command) {
            is Command.Put -> {
                dbHelper.addValue(command.key, command.value, command.valType)
            }
            is Command.Clear -> {
                dbHelper.clear()
            }
            is Command.Remove -> {
                dbHelper.removeValue(command.key)
            }
        }
    }

    override fun apply(): Completable {
        return Completable.fromCallable {
            dbHelper.beginTransaction()
            try {
                commands.forEach {
                    processCommand(it)
                }

                dbHelper.setTransactionSuccessful()

                notifyCommandsDone(commands)

                commands.clear()
            } finally {
                dbHelper.endTransaction()
            }
        }
    }

}