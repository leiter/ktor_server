package cut.the.crap.repositories

import cut.the.crap.data.RefreshTokenResponse
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class RefreshTokenRepository(db: CoroutineDatabase) : Repository<RefreshTokenResponse> {
    override lateinit var mongoCollection: CoroutineCollection<RefreshTokenResponse>

    init {
        mongoCollection = db.getCollection()
    }

    suspend fun setOrUpdate(entry: RefreshTokenResponse) : RefreshTokenResponse {
        val refreshToken = mongoCollection.findOneById(entry.id)
        return refreshToken?.let {
            update(entry)
        } ?: add(entry)
    }

}