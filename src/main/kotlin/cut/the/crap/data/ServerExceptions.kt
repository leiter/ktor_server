package cut.the.crap.data

import cut.the.crap.common.User
import io.ktor.http.*

@kotlinx.serialization.Serializable
sealed class ServerErrorMessage(val message: String, val errorCode: Int) {
    @kotlinx.serialization.Serializable
    data class UserEmailAlreadyExists(val anonymousInternalUser: User) : ServerErrorMessage(
        "There is already a user with this email address.", HttpStatusCode.Conflict.value)
    @kotlinx.serialization.Serializable
    object FileUploadFailed : ServerErrorMessage(
        "Authentication is missing", HttpStatusCode.Conflict.value)
}