package cut.the.crap.data

class UserNotFoundException(msg: String? = null, throwable: Throwable? = null) : Exception(msg, throwable)

class PropertyNotFoundException(message: String) : Throwable(message)

class MemberAlreadyExistsException: Exception(
    "There is already a member with that username in the room."
)