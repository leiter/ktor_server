package cut.the.crap.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cut.the.crap.repositories.MessageDataRepository
import cut.the.crap.repositories.InternalUserRepository
import cut.the.crap.session.ChatSession
import io.ktor.sessions.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.koin.ktor.ext.inject


//todo implement stricter checks
fun Application.configureSecurity() {

    val messageDataRepository by inject<MessageDataRepository>()
    val internalUserRepository by inject<InternalUserRepository>()

    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(ApplicationCallPipeline.Features) {
        val sessionId = messageDataRepository.getChatRoom(call.parameters["sessionId"]).id
//        messageDataRepository.getAllChatMessages(sessionId)
        if (call.sessions.get<ChatSession>() == null ) {
            val username = call.parameters["username"] ?: "Guest"
            call.sessions.set(ChatSession(username, sessionId))
        }
    }


    install(Authentication) {
        // Either set -Djwt.secret=whatever in the run configuration or access it like the other properties or hardcode.
        // There are only two occasions
        val secret = environment.config.property("jwt.secret").getString()  //  System.getProperty("jwt.secret")!! //
        val issuer = environment.config.property("jwt.issuer").getString()
        val audience = environment.config.property("jwt.audience").getString()
        val myRealm = environment.config.property("jwt.realm").getString()

        val jwtVerifier = JWT  // make it a function
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)

            .withIssuer(issuer)
            .build()

        jwt("auth-jwt") {
            realm = myRealm
            verifier(jwtVerifier)
            validate { jwtCredential ->
                val user = internalUserRepository.getUser(jwtCredential.payload.subject)  // userId
//                if (user != null) {
                if (jwtCredential.payload.getClaim("username").asString() != "" ) {
                    JWTPrincipal(jwtCredential.payload)
                } else {
                    null
                }
            }
        }

//        jwt("chat-authorization") {
////            realm = myRealm
//            verifier(jwtVerifier)
//            validate { jwtCredential ->
//                val user = userRepository.getById(jwtCredential.payload.getClaim("userId").asString())
//                if (user != null) {
//                    JWTPrincipal(jwtCredential.payload)
//                } else {
//                    null
//                }
//            }
//        }

    }
}
