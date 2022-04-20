package cut.the.crap.repositories

import cut.the.crap.data.RefreshToken
import cut.the.crap.data.ShoutOut
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class ShoutOutRepository(db: CoroutineDatabase) : Repository<ShoutOut> {

    override lateinit var mongoCollection: CoroutineCollection<ShoutOut>

    init {
        mongoCollection = db.getCollection()
    }


}