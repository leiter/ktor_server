package cut.the.crap

import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.network.tls.extensions.HashAlgorithm
import io.ktor.network.tls.extensions.SignatureAlgorithm
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import java.io.File
import java.security.KeyStore

class Server(args: Array<String>) {

    private val pass = "abcd1234"

    private val server: NettyApplicationEngine

    init {
        server = createServer(args)
    }

    private fun createServer(args: Array<String>): NettyApplicationEngine {
        val alias = "certificateAlias"

        val keystore = buildKeyStore {
            certificate(alias) {
                hash = HashAlgorithm.SHA256
                sign = SignatureAlgorithm.ECDSA
                keySizeInBits = 256
                password = pass
            }
        }

        val server = embeddedServer(Netty, applicationEngineEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("application.conf"))  // config.config("application.conf")
//            config = MapApplicationConfig(
//                "jwt.issuer" to "application.conf",
//                "jwt.secret" to "secret",
//                "jwt.issuer" to "http://0.0.0.0:8181/",
//                "jwt.audience" to "http://0.0.0.0:8181/hello",
//                "jwt.realm" to "Access to 'hello'",
//                "jwt.access.lifetime" to "5",
//                "jwt.refresh.lifetime" to "175",
//            )

            sslConnector(keystore,
                alias,
                { "".toCharArray() },
                { pass.toCharArray() }) {
                port = 8181
                keyStorePath = keyStore.asFile.absoluteFile

                module {
                    module()
                }
            }
        })

        return server
    }

    fun start() {
        server.start(wait = true)
    }

    private val KeyStore.asFile: File
        get() {
            val keyStoreFile = File("build/temp.jks")
            this.saveToFile(keyStoreFile, pass)
            return keyStoreFile
        }
}