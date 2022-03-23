package cut.the.crap.repositories

import cut.the.crap.data.User
import cut.the.crap.data.UserNotFoundException
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserRepository(db: CoroutineDatabase) : Repository<User> {

    override lateinit var mongoCollection: CoroutineCollection<User>

    init {
        mongoCollection = db.getCollection()
    }

    suspend fun getUserByEmail(email: String? = null): User? {
        return try {
            mongoCollection.findOne(User::email eq email)
        } catch (t: Throwable) {
            throw UserNotFoundException("Cannot get user with that email", t)
        }
    }
}

