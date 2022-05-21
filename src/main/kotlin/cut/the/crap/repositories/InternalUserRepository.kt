package cut.the.crap.repositories

import cut.the.crap.common.InternalUser
import cut.the.crap.data.UserNotFoundException
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class InternalUserRepository(db: CoroutineDatabase) : Repository<InternalUser> {

    override lateinit var mongoCollection: CoroutineCollection<InternalUser>

    init {
        mongoCollection = db.getCollection()
    }

    suspend fun getUserByEmail(email: String? = null): InternalUser? {
        return try {
            mongoCollection.findOne(InternalUser::email eq email)
        } catch (t: Throwable) {
            throw UserNotFoundException("Cannot get user with that email", t)
        }
    }

    suspend fun getUser(id: String? = null): InternalUser? {
        return try {
            mongoCollection.findOne(InternalUser::_id eq id)
        } catch (t: Throwable) {
            throw UserNotFoundException("Cannot get user with that email", t)
        }
    }
}

