package cut.the.crap

import cut.the.crap.di.mainModule
import cut.the.crap.plugins.*
import io.ktor.application.*
import org.koin.ktor.ext.Koin
import java.time.Duration

fun getEnvironmentString(propertyName: String, context: ApplicationCall) : String {
    val environment = context.application.environment
    return environment.config.property(propertyName).getString()
}

fun longProperty(path: String, context: ApplicationCall): Long =
    getEnvironmentString(path, context).toLong()

fun Long.withOffset(offset: Duration) = this + offset.toMillis()


fun main(args: Array<String>): Unit = // Server(args).start() // this is the implementation with https
    io.ktor.server.netty.EngineMain.main(args)

//@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }
    configureSockets()
    configureSecurity()
    configureRouting()
    configureSerialization()
    configureMonitoring()
}
