package cut.the.crap.routes

import cut.the.crap.common.User
import cut.the.crap.repositories.InternalUserRepository
import cut.the.crap.repositories.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.user(
    internalUserRepository: InternalUserRepository,
    userRepository: UserRepository
) {

    authenticate("auth-jwt") {

        post("/user") {

            val userData = call.receive<User>()
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.subject!! //getClaim("username").asString()
            val internalUser = internalUserRepository.getById(userId)!!
//            val user = userRepository.getById(internalUser.id)
            userRepository.updateUser(userData)
//            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
//            call.respondText("Hello, $username! Token is expired at $expiresAt ms.\n")
        }


    }

    post( "/friends") {
        val userData = call.receive<User>()
        call.respond(userRepository.getFriends(userData.contacts))
    }
}
