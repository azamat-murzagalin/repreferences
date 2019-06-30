package com.iamoem.repreferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.iamoem.repreference.core.getRePreferences
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rePreference = getRePreferences("testname")
        val testKey = "test key"
        val testIntKey = "test int key"
        val testBoolKey = "test bool key"

        rePreference.observeString(testKey, "some default value")
            .subscribeOn(Schedulers.io())
            .subscribe ({
                Log.d("repref", "observed value1 (${Thread.currentThread().toString()}): $it")
            }, {
                Log.e("repref", "observed value1 $it")
            })

        rePreference.observeBoolean(testBoolKey, false)
            .subscribeOn(Schedulers.io())
            .subscribe ({
                Log.d("repref", "observed bool (${Thread.currentThread().toString()}): $it")
            }, {
                Log.e("repref", "observed bool $it")
            })


        rePreference.observeInt(testIntKey, 1)
            .subscribeOn(Schedulers.io())
            .subscribe ({
                Log.d("repref", "observed int (${Thread.currentThread().toString()}): $it")
            }, {
                Log.e("repref", "observed int $it")
            })


        rePreference.observeString(testKey, "some default value")
            .subscribeOn(Schedulers.io())
            .subscribe ({
                Log.d("repref", "observed value2 (${Thread.currentThread().toString()}): $it")
            }, {
                Log.e("repref", "observed value2 $it")
            })

        val sub = rePreference.observeString(testKey, "some default value")
            .subscribeOn(Schedulers.io())
            .subscribe ({
                Log.d("repref", "observed value3 (${Thread.currentThread().toString()}): $it")
            }, {
                Log.e("repref", "observed value3 $it")
            })



        Thread.sleep(5000)

        rePreference.edit()
            .putString(testKey, "test val")
            .putBoolean(testBoolKey, true)
            .putInt(testIntKey, 2)
            .commit()

        Thread.sleep(5000)

        sub.dispose()

        rePreference.edit().putString(testKey, "test val 2")
            .putBoolean(testBoolKey, false)
            .putInt(testIntKey, 3)
            .commit()

    }
}
