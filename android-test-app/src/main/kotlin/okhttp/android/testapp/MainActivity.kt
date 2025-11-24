/*
 * Copyright (C) 2023 Block, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp.android.testapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import kotlin.concurrent.thread
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.gm.GmOkHttpClient
import okhttp3.internal.platform.AndroidPlatform
import okio.IOException

open class MainActivity : ComponentActivity() {
  private val TAG = "MainActivity"
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    thread {
      okhttpKona()
    }
    val client = OkHttpClient()

    // Ensure we are compiling against the right variant
    println(AndroidPlatform.isSupported)

    val url = "https://github.com/square/okhttp".toHttpUrl()
    println(url.topPrivateDomain())

    client.newCall(Request(url)).enqueue(
      object : Callback {
        override fun onFailure(
          call: Call,
          e: IOException,
        ) {
          println("failed: $e")
        }

        override fun onResponse(
          call: Call,
          response: Response,
        ) {
          println("response: ${response.code}")
          response.close()
        }
      },
    )
  }

  private fun okhttpKona() {
    try {
      val client =
        GmOkHttpClient.getOkHttpClientBuilder(resources.assets.open("sm2.trust.pem"))
          .build()
      val request = Request.Builder()
        .get()
        .url("https://demo.gmssl.cn:1443").build()
      val response = client.newCall(request).execute()
      val string = response.body.string()
      Log.e(TAG, "okhttpKona: " + string)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
