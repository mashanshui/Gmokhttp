## GmOkhttp
基于最新的okhttp（5.3.0）改造的支持国密TLS的版本，除了添加了国密TLS支持外，其他功能与okhttp一致，可以直接替换原有项目中的okhttp。

## 引入
 ```kotlin
 implementation ("com.io.github.mashanshui:gmokhttp:1.0.0")
 ```
由于包名没有修改，所以需要将原来的okhttp依赖删除，注意这里如果有其他库引用了okhttp也要排除掉，例如：
 ```kotlin
 implementation ("com.squareup.retrofit2:retrofit:3.0.0") {
  exclude(group = "com.squareup.okhttp3", module = "okhttp")
}
 ```
如果引入的地方较多，可以全局排除：
```kotlin
configurations.all {
  exclude(group = "com.squareup.okhttp3", module = "okhttp")
}
 ```

## 使用
```kotlin
val client =
  GmOkHttpClient.getOkHttpClientBuilder(resources.assets.open("sm2.trust.pem"))
    .build()
val request = Request.Builder()
  .url("https://demo.gmssl.cn:1443")
  .build()
val response = client.newCall(request).execute()
```
```kotlin
val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
  override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
  }

  override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
  }

  override fun getAcceptedIssuers(): Array<X509Certificate> {
    return arrayOf()
  }
})
val client =
  GmOkHttpClient.getOkHttpClientBuilder(trustAllCerts)
    .build()
val request = Request.Builder()
  .url("https://demo.gmssl.cn:1443")
  .build()
val response = client.newCall(request).execute()
```
通过GmOkHttpClient.getOkHttpClientBuilder(caCert)并传入ca证书获取支持国密的OkHttpClient，也可通过getOkHttpClientBuilder(trustManagers)方法自定义证书验证逻辑。

后续使用和普通okhttp没有区别，理论上也支持Retrofit等框架，但未测试。

## 原理介绍
https://blog.csdn.net/shanshui911587154/article/details/155204476
