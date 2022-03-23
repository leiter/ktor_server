package cut.the.crap.data

import io.ktor.http.cio.websocket.*
import org.bson.types.ObjectId
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

interface Model {
    val id: String
}

// Incoming and Outgoing
@Serializable
data class User(
    override val id: String = ObjectId().toString(),
    val email: String,
    val hashedPassword: String,  // will go soon
    var isAnonymous: Boolean = true,
) : Model





@Serializable
data class Message(
    val text: String,
    val username: String,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString()
)

@Serializable
data class Location(
    val latitude: Long,
    val longitude: Long,
    @BsonId
    val id: String = ObjectId().toString()
)

@Serializable
data class Appointment(
    val location: Location,
    val username: String,
    val userId: Long,
    @BsonId
    val id: String = ObjectId().toString()
)

// Incoming Models
@Serializable
data class UserLoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RefreshTokenRequest(
    override val id: String,
    val refreshToken: String,
) : Model




// Outgoing
@Serializable
data class UserResponse(
    val tokenPair: TokenPairResponse,
    val user: User
)

@Serializable
data class TokenPairResponse(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class RefreshTokenResponse(
    override val id: String,
    val refreshToken: String,
    val expiresAt: Long
) : Model {
    val userId: String = id
}

internal data class Member(
    val username: String,
    val sessionId: String,
    val socket: WebSocketSession
)




