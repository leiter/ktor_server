package cut.the.crap.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cut.the.crap.session.ChatSession
import io.ktor.sessions.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.util.*

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(ApplicationCallPipeline.Features) {
        if(call.sessions.get<ChatSession>() == null) {
            val username = call.parameters["username"] ?: "Guest"
            call.sessions.set(ChatSession(username, generateNonce()))
        }
    }

    install(Authentication){
        // Either set -Djwt.secret=whatever in the run configuration or access it like the other properties or hardcode.
        // There are only two occasions
        val secret = System.getProperty("jwt.secret")!! // environment.config.property("jwt.secret").getString()
        val issuer = environment.config.property("jwt.issuer").getString()
        val audience =  environment.config.property("jwt.audience").getString()
        val myRealm =  environment.config.property("jwt.realm").getString()

        jwt("auth-jwt") {
            realm = myRealm
            verifier(JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
            )
            validate { jwtCredential ->
                if (jwtCredential.payload.subject != ""){
                    JWTPrincipal(jwtCredential.payload)
                } else {
                    null
                }
            }
        }
    }
}
