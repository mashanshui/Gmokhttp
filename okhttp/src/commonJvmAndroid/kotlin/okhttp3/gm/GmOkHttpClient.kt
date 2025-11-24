package okhttp3.gm

import java.io.InputStream
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion

/**
 * @author mashanshui
 * @since 2025/11/14
 */
object GmOkHttpClient {
  fun getOkHttpClientBuilder(certChain: InputStream): OkHttpClient.Builder {
    val sslContext = GMSSLContext(certChain)
    val trustManagers =
      sslContext.trustManagers ?: throw NullPointerException("trustManagers == null")
    return getOkHttpClientBuilder(sslContext.sslSocketFactory, trustManagers)
  }

  fun getOkHttpClientBuilder(certChain: String): OkHttpClient.Builder {
    val sslContext = GMSSLContext(certChain)
    val trustManagers =
      sslContext.trustManagers ?: throw NullPointerException("trustManagers == null")
    return getOkHttpClientBuilder(sslContext.sslSocketFactory, trustManagers)
  }

  fun getOkHttpClientBuilder(trustManagers: Array<TrustManager>): OkHttpClient.Builder {
    val sslContext = GMSSLContext(trustManagers)
    return getOkHttpClientBuilder(sslContext.sslSocketFactory, trustManagers)
  }

  private fun getOkHttpClientBuilder(sslSocketFactory: SSLSocketFactory, trustManagers: Array<TrustManager>): OkHttpClient.Builder {
    val spec = (ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS))
      .tlsVersions(TlsVersion.TLCPv1_1)
      .cipherSuites(CipherSuite.TLCP_ECC_SM4_GCM_SM3, CipherSuite.TLCP_ECC_SM4_CBC_SM3)
      .build()
    return OkHttpClient.Builder()
      .sslSocketFactory(
        sslSocketFactory,
        (trustManagers[0] as javax.net.ssl.X509TrustManager?)!!
      )
      .connectionSpecs(mutableListOf<ConnectionSpec>(spec))
      .hostnameVerifier(HostnameVerifier { hostname: String?, session: SSLSession? -> true })
  }
}
