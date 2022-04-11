package cut.the.crap.data

import io.ktor.http.cio.websocket.*
import org.bson.types.ObjectId
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.regex.Pattern.compile


interface Model {
    val id: String
}



// Incoming and Outgoing
@Serializable
data class User(
    @BsonId
    override val id: String = ObjectId().toString(),
    val email: String = "",
    val hashedPassword: String,
    var isAnonymous: Boolean,
    val contacts: List<String> = emptyList(),
    val chatIds: List<String> = emptyList()
) : Model


@Serializable
data class ChatRoom(
    @BsonId override val id: String = ObjectId().toString(),
    val userAccess: Map<String, Long> = hashMapOf(),  // userId and timestamp
    val allMessages: List<Message> = emptyList()
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
data class RegisterUserResponse(
    val tokenPair: TokenPairResponse,
    val user: User
)

@Serializable
data class ChatToken(
    val token: String
)

@Serializable
data class TokenPairResponse(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class RefreshToken(
    override val id: String,
    val refreshToken: String,
    val expiresAt: Long
) : Model {
    val userId: String = id
}

@Serializable
data class RefreshTokenResponse(
    val refreshToken: String,
)

internal data class Member(
    val username: String,
    val sessionId: String,
    val socket: WebSocketSession
)


private val otherMatcher = compile(
    "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
)

//(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])

private val emailRegex = compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

//emailRegex.matcher("john.doe@mail.com").matches()