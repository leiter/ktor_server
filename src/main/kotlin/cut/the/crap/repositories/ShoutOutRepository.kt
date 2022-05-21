package cut.the.crap.repositories

import cut.the.crap.common.ShoutOut
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class ShoutOutRepository(db: CoroutineDatabase) : Repository<ShoutOut> {

    override lateinit var mongoCollection: CoroutineCollection<ShoutOut>

    init {
        mongoCollection = db.getCollection()
    }

    override suspend fun getAll(): List<ShoutOut> {
        return super.getAll().reversed()
    }

}