package cut.the.crap.routes

import at.favre.lib.crypto.bcrypt.BCrypt
import cut.the.crap.common.InternalUser
import cut.the.crap.common.RefreshToken
import cut.the.crap.common.User
import cut.the.crap.data.*
import cut.the.crap.data.ServerErrorMessage.UserEmailAlreadyExists
import cut.the.crap.repositories.InternalUserRepository
import cut.the.crap.repositories.RefreshTokenRepository
import cut.the.crap.repositories.UserRepository
import cut.the.crap.requests.RefreshTokenRequest
import cut.the.crap.requests.UserLoginRequest
import cut.the.crap.response.RegisterUserResponse
import cut.the.crap.utils.generateTokenPair
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.nio.charset.StandardCharsets
import java.util.*

fun Route.login(internalUserRepository: InternalUserRepository, refreshTokenRepository: RefreshTokenRepository) {

    post("/login") {
        val loginInput = call.receive<UserLoginRequest>()
        val user = internalUserRepository.getUserByEmail(loginInput.email)  // getById for anonymous
        if (user != null && BCrypt.verifyer().verify(
                loginInput.password.toCharArray(),
                Base64.getDecoder().decode(user.hashedPassword)
            ).verified
        ) {
            call.respond(generateTokenPair(user._id, context, refreshTokenRepository))
        } else call.respond(HttpStatusCode.Unauthorized)  // add error message
    }
}

fun Route.refreshToken(refreshTokenRepository: RefreshTokenRepository) {
    post("/refresh") {
        val oldRefreshToken = call.receive<RefreshTokenRequest>()

        val token: RefreshToken? =
            try {
                refreshTokenRepository.getById(oldRefreshToken._id)
            } catch (e: PropertyNotFoundException) {
                null
            }

        val currentTime = System.currentTimeMillis()
//
        if (token != null) {     //    && token.expiresAt > currentTime
            val tokenPair = generateTokenPair(token.userId, context, refreshTokenRepository)
            call.respond(tokenPair)
        } else
        // create Error Model
            call.respondText(
                """
                    {
                        "description": "invalid token"
                    }
                    """.trimIndent(),
                ContentType.parse("application/json"),
                HttpStatusCode.BadRequest
            )
    }
}

fun Route.unregister(internalUserRepository: InternalUserRepository, refreshTokenRepository: RefreshTokenRepository) {
    authenticate {
        post("/unregister") {

        }
    }

}

fun Route.register(
    userRepository: UserRepository,
    internalUserRepository: InternalUserRepository,
    refreshTokenRepository: RefreshTokenRepository
) {
    post("/register") {
        // add validation
        // check if anonymous login

        val loginInput = call.receive<UserLoginRequest>()
        val savedUser = if (loginInput.email.isNotBlank())
            internalUserRepository.getUserByEmail(loginInput.email)
        else null

        if (savedUser == null) {
            val hashedPassword = Base64.getEncoder().encodeToString(
                BCrypt.withDefaults().hash(10, loginInput.password.toByteArray(StandardCharsets.UTF_8))
            )
            val exposedUser = userRepository.add(User(isAnonymous = false, email = loginInput.email))
            val internalUser = internalUserRepository.add(
                InternalUser(
                    email = loginInput.email,
                    hashedPassword = hashedPassword,
                    exposedId = exposedUser._id
                )
            )
            val tokenPair = generateTokenPair(internalUser._id, context, refreshTokenRepository)
            call.respond(RegisterUserResponse(tokenPair, exposedUser))
        } else {
//            error("No such user by that email")
            val exposedUser = userRepository.add(User(isAnonymous = true))
            val newInternalUser = InternalUser(email = "", hashedPassword = "", exposedId = exposedUser._id)
            internalUserRepository.add(newInternalUser)
            call.respond(HttpStatusCode.Conflict, UserEmailAlreadyExists(exposedUser))
        }
    }

//    fun verifyToken(call: ApplicationCall): User? {
//        return try {
//            val authHeader = call.request.headers["Authorization"] ?: ""
//            val token = authHeader.split("Bearer ").last()
//            val accessToken = verifier.verify(JWT.decode(token))
//            val userId = accessToken.getClaim("userId").asString()
//            return User(id = userId, email = "", hashedPassword = "")
//        } catch (e: Exception) {
//            print(e.message)
//            null
//        }
//    }

}

