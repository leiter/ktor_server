package cut.the.crap.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cut.the.crap.common.RefreshToken
import cut.the.crap.getEnvironmentString
import cut.the.crap.longProperty
import cut.the.crap.repositories.RefreshTokenRepository
import cut.the.crap.response.TokenPairResponse
import cut.the.crap.withOffset
import io.ktor.application.*
import java.time.Duration
import java.util.*


suspend fun generateTokenPair(
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
        .withClaim("username", userId)
        .withAudience(audience)
        .withExpiresAt(Date(currentTime.withOffset(Duration.ofMinutes(accessLifetime))))
        .withIssuer(issuer)
        .sign(secret)

    val refreshToken = UUID.randomUUID().toString()  // create JWT
    refreshTokenRepository.setOrUpdate(
        RefreshToken(
            _id = userId,
            refreshToken = refreshToken,
            expiresAt = currentTime.withOffset(Duration.ofDays(refreshLifetime))
        )
    )
    return TokenPairResponse(accessToken, refreshToken)
}

suspend fun generateChatAccess(
    sessionId: String,
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
        .withSubject(sessionId)
        .withAudience(audience)
        .withExpiresAt(Date(currentTime.withOffset(Duration.ofMinutes(accessLifetime))))
        .withIssuer(issuer)
        .sign(secret)

    val refreshToken = UUID.randomUUID().toString()  // create JWT
    refreshTokenRepository.setOrUpdate(
        RefreshToken(
            _id = sessionId,
            refreshToken = refreshToken,
            expiresAt = currentTime.withOffset(Duration.ofDays(refreshLifetime))
        )
    )
    return TokenPairResponse(accessToken, refreshToken)
}

fun createJwtToken(tokenData: TokenData, context: ApplicationCall): String {
    val secret =
        Algorithm.HMAC256(System.getProperty("jwt.secret")!!) // environment.config.property("jwt.secret").getString()
    val currentTime = System.currentTimeMillis()
    val issuer: String = getEnvironmentString("jwt.issuer", context)
    val audience = getEnvironmentString("jwt.audience", context)
    val accessLifetime = longProperty("jwt.access.lifetime", context)    // minutes
    val refreshLifetime = longProperty("jwt.refresh.lifetime", context)  // days

    return when (tokenData) {
        is TokenData.AccessTokenPair -> {
            JWT.create()
                .withSubject(tokenData.userId)
                .withAudience(audience)
                .withExpiresAt(Date(currentTime.withOffset(Duration.ofMinutes(accessLifetime))))
                .withIssuer(issuer)
                .sign(secret)
        }
        is TokenData.ChatAccessToken -> {
            JWT.create()
                .withSubject(tokenData.sessionId)
                .withAudience(audience)
                .withExpiresAt(Date(currentTime.withOffset(Duration.ofMinutes(accessLifetime))))
                .withIssuer(issuer)
                .sign(secret)
        }
        is TokenData.RefreshToken -> {
            JWT.create()
                .withSubject(tokenData.userId)
                .withAudience(audience)
                .withExpiresAt(Date(currentTime.withOffset(Duration.ofMinutes(refreshLifetime))))
                .withIssuer(issuer)
                .sign(secret)

        }
    }

}

sealed class TokenData {
    data class AccessTokenPair(
        val userId: String,
    ) : TokenData()

    data class RefreshToken(
        val userId: String,
    ) : TokenData()

    data class ChatAccessToken(
        val sessionId: String,
    ) : TokenData()


}