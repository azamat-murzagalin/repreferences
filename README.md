# RePreferences
[![](https://jitpack.io/v/AzamatEm/repreferences.svg)](https://jitpack.io/#AzamatEm/repreferences)

Library to save key-value pairs with SharedPreferences-like api. But this library uses SQLite to save values, and adds reactive support

# Add the SDK to your Android project

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

```gradle
dependencies {
        implementation 'com.github.AzamatEm:repreferences:0.8.0'
}
```

# Initialize 



```kotlin
import com.iamoem.repreference.core.getRePreferences

...

val rePreference = getRePreferences("filename")
```

In this case filename is name of SQLite db file, so be sure that you are not using same names for your own databases



# Read data

You can read data using methods similar to SharePreferences:  


```kotlin
fun getBoolean(key: String, defValue: Boolean): Boolean
fun getFloat(key: String, defValue: Float): Float
fun getInt(key: String, defValue: Int): Int
fun getLong(key: String, defValue: Long): Long
fun getString(key: String, defValue: String): String
fun getStringSet(key: String, defValue: Set<String>): Set<String>
```

or you can observe values using methods:

```kotlin
fun observeBoolean(key: String, defValue: Boolean): Flowable<Boolean>
fun observeFloat(key: String, defValue: Float): Flowable<Float>
fun observeInt(key: String, defValue: Int): Flowable<Int>
fun observeLong(key: String, defValue: Long): Flowable<Long>
fun observeString(key: String, defValue: String): Flowable<String>
fun observeStringSet(key: String, defValue: Set<String>): Flowable<Set<String>>
```

and be aware that in all observe methods the library reads data in Schedulers.io() scheduler by default, so if you want to do any UI changes after subscribing, do not forget to switch to android main thread



# Write data

Writing data is similar to SharedPreferences:

## Step 1. Create editor:

```kotlin
var editor = rePreference.edit()
```

## Step 2. Write data:
```kotlin
editor = editor.putString(testKey, "test val")
               .putBoolean(testBoolKey, true)
               .putInt(testIntKey, 2)
```

## Step 3. Apply changes:

Here you have two choices:
* Use commit function:
    
    ```kotlin
    editor.commit()
    ```

    Similar to Shared preferences, it returns Boolean value signaling if commit was successful. And be aware that commit() writes all data in the same thread that called it, so you have to care about it by yourself 
    
* Use apply function:
    ```kotlin
    editor.apply()
          .subscribe({ 
              //write was succesfull
          }, {
              //any error happened while writing
          })
    ```
    apply() function returns Completable type, so you can choose which Scheduler to use
    
    
# License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

