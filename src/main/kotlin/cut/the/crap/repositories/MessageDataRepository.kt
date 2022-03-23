package cut.the.crap.repositories

import cut.the.crap.data.Message
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class MessageDataRepository(
    db: CoroutineDatabase
): Repository<Message> {

    override lateinit var mongoCollection: CoroutineCollection<Message>

    init {
        mongoCollection = db.getCollection()
    }

    override suspend fun getAll(): List<Message> {
        return mongoCollection.find()
            .descendingSort(Message::timestamp)
            .toList()
    }
}