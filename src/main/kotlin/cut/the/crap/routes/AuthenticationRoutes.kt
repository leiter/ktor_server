package cut.the.crap.routes

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cut.the.crap.data.*
import cut.the.crap.data.ServerErrorMessage.UserEmailAlreadyExists
import cut.the.crap.getEnvironmentString
import cut.the.crap.longProperty
import cut.the.crap.repositories.RefreshTokenRepository
import cut.the.crap.repositories.UserRepository
import cut.the.crap.withOffset
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.*

fun Route.login(userRepository: UserRepository, refreshTokenRepository: RefreshTokenRepository) {

    post("/login") {
        val loginInput = call.receive<UserLoginRequest>()
        val user = userRepository.getUserByEmail(loginInput.email)  // getById for anonymous
        if (user != null && BCrypt.verifyer().verify(
                loginInput.password.toCharArray(),
                Base64.getDecoder().decode(user.hashedPassword)
            ).verified
        ) {
            call.respond(generateTokenPair(user.id, context, refreshTokenRepository))
        } else call.respond(HttpStatusCode.Unauthorized)  // add error message
    }

    authenticate("auth-jwt") {
        get("/hello") {
            val principal = call.principal<JWTPrincipal>()!!
            val username = principal.payload.getClaim("username").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
        }
    }
}

fun Route.refreshToken(refreshTokenRepository: RefreshTokenRepository) {
    post("/refresh") {
        val oldRefreshToken = call.receive<RefreshTokenRequest>()

        val token: RefreshTokenResponse? =
            try {
                refreshTokenRepository.getById(oldRefreshToken.id)
            } catch (e: PropertyNotFoundException) {
                null
            }

        val currentTime = System.currentTimeMillis()

//                && token.expiresAt > currentTime
        if (token != null ) {
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

fun Route.unregister(userRepository: UserRepository, refreshTokenRepository: RefreshTokenRepository) {
    authenticate {
        post("/unregister") {

        }
    }

}

fun Route.register(userRepository: UserRepository, refreshTokenRepository: RefreshTokenRepository) {
    post("/register") {
        // add validation
        // check if anonymous login

        val loginInput = call.receive<UserLoginRequest>()
        val savedUser = userRepository.getUserByEmail(loginInput.email)

        if (savedUser == null) {
            val hashedPassword = Base64.getEncoder().encodeToString(
                BCrypt.withDefaults().hash(10, loginInput.password.toByteArray(StandardCharsets.UTF_8))
            )
            val user = userRepository.add(User(email = loginInput.email, hashedPassword = hashedPassword, isAnonymous = false))

            val tokenPair = generateTokenPair(user.id, context, refreshTokenRepository)

            call.respond(UserResponse(tokenPair, user))
        } else {
//            error("No such user by that email")
            val newUser = User(email = "", hashedPassword = "", isAnonymous = true)

            call.respond(HttpStatusCode.Conflict, UserEmailAlreadyExists(newUser))
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


private suspend fun generateTokenPair(
    userId: String,
    context: ApplicationCall,
    refreshTokenRepository: RefreshTokenRepository
): TokenPairResponse {

    val currentTime = System.currentTimeMillis()

    val issuer: String = getEnvironmentString("jwt.issuer", context)
    val secret = Algorithm.HMAC256(getEnvironmentString("jwt.secret", context))
    val audience = getEnvironmentString("jwt.audience", context)

    val accessLifetime = longProperty("jwt.access.lifetime", context)    // minutes
    val refreshLifetime = longProperty("jwt.refresh.lifetime", context)  // days
//    val dbUsername = stringProperty("authDB.username")
//    val dbPassword = stringProperty("authDB.password")

    val accessToken = JWT.create()
        .withSubject(userId)
        .withAudience(audience)
        .withExpiresAt(Date(currentTime.withOffset(Duration.ofMinutes(accessLifetime))))
        .withIssuer(issuer)
        .sign(secret)

    val refreshToken = UUID.randomUUID().toString()
    refreshTokenRepository.setOrUpdate(
        RefreshTokenResponse(
            id = userId,
            refreshToken = refreshToken,
            expiresAt = currentTime.withOffset(Duration.ofDays(refreshLifetime))
        )
    )
    return TokenPairResponse(accessToken, refreshToken)
}
