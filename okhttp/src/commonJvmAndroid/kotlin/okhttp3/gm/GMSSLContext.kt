package okhttp3.gm

import com.tencent.kona.crypto.KonaCryptoProvider
import com.tencent.kona.pkix.KonaPKIXProvider
import com.tencent.kona.ssl.KonaSSLProvider
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory


/**
 * @author mashanshui
 * @since 2025/11/14
 */
class GMSSLContext {
  var trustManagers: Array<TrustManager>? = null
    private set
  var sslContext: SSLContext? = null
    private set

  constructor(certChain: String) : this(ByteArrayInputStream(certChain.toByteArray()))

  constructor(certChain: InputStream) {
    try {
      val trustStore: KeyStore = createTrustStore(certChain)
      val tmf = TrustManagerFactory.getInstance("PKIX", "KonaSSL")
      tmf.init(trustStore)
      trustManagers = tmf.getTrustManagers()
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
    initContext()
  }

  constructor(trustManagers: Array<TrustManager>?) {
    this.trustManagers = trustManagers
    initContext()
  }

  private fun initContext() {
    try {
      val context = SSLContext.getInstance("TLCPv1.1", "KonaSSL")
      context.init(null, trustManagers, SecureRandom())
      sslContext = context
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }


  val sslSocketFactory: SSLSocketFactory
    get() = sslContext!!.getSocketFactory()

  companion object {
    init {
      Security.addProvider(KonaCryptoProvider())
      Security.addProvider(KonaPKIXProvider())
      Security.addProvider(KonaSSLProvider())
    }

    @Throws(Exception::class)
    private fun createTrustStore(certIn: InputStream): KeyStore {
      val trustStore = KeyStore.getInstance("PKCS12", "KonaPKIX")
      trustStore.load(null, null)
      val cf = CertificateFactory.getInstance("X.509", "KonaPKIX")
      val certs = cf.generateCertificates(certIn)

      var index = 0
      for (cert in certs) {
        if (cert is X509Certificate) {
          // 将每个CA证书添加到信任库
          trustStore.setCertificateEntry("ca-alias-" + index++, cert)
        }
      }
      return trustStore
    }
  }
}
