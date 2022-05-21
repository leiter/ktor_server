package cut.the.crap.repositories

import cut.the.crap.common.RefreshToken
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class RefreshTokenRepository(db: CoroutineDatabase) : Repository<RefreshToken> {
    override lateinit var mongoCollection: CoroutineCollection<RefreshToken>

    init {
        mongoCollection = db.getCollection()
    }

    suspend fun setOrUpdate(entry: RefreshToken) : RefreshToken {
        val refreshToken = mongoCollection.findOneById(entry._id)
        return refreshToken?.let {
            update(entry)
        } ?: kotlin.run {
            delete(entry._id)
            add(entry)
        }
    }

}