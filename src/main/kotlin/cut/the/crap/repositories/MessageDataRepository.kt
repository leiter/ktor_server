package cut.the.crap.repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import cut.the.crap.data.ChatRoom
import cut.the.crap.data.Message
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq


class MessageDataRepository(
    db: CoroutineDatabase
): Repository<ChatRoom> {

    override lateinit var mongoCollection: CoroutineCollection<ChatRoom>

    init {
        mongoCollection = db.getCollection()
    }

    suspend fun getChatRoom(sessionId: String?) : ChatRoom {
        if (sessionId.isNullOrEmpty()) return  add(ChatRoom())
        val room = mongoCollection.findOne(ChatRoom::id eq sessionId)
        return room ?: kotlin.run {
//            add(ChatRoom(id = sessionId))
            add(ChatRoom())
        }
    }

    suspend fun getAllChatMessages(sessionId: String?): List<Message> {
        return if(sessionId==null) emptyList() else getChatRoom(sessionId).allMessages.reversed()
    }

    suspend fun addChatMessage(message: Message, sessionId: String) {
        val chat = getChatRoom(sessionId)
        val list = chat.allMessages.toMutableList()
        list.add(message)
        val update = Updates.push("allMessages",message)
        mongoCollection.updateOne(Filters.eq("_id", sessionId),update)
    }
}