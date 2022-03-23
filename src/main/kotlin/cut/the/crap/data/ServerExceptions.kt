package cut.the.crap.data

import io.ktor.http.*


sealed class ServerErrorMessage(val message: String, val errorCode: Int) {
    data class UserEmailAlreadyExists(val anonymousUser: User) : ServerErrorMessage(
        "There is already a user with this email address.", HttpStatusCode.Conflict.value)
    object FileUploadFailed : ServerErrorMessage(
        "Authentication is missing", HttpStatusCode.Conflict.value)
}