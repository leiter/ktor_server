package cut.the.crap.repositories

import cut.the.crap.data.RefreshToken
import cut.the.crap.data.User
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class UserRepository(db: CoroutineDatabase) : Repository<User> {

    override lateinit var mongoCollection: CoroutineCollection<User>

    init {
        mongoCollection = db.getCollection()
    }





}