package cut.the.crap.repositories

import cut.the.crap.Model
import cut.the.crap.PersonData
import cut.the.crap.common.User
import cut.the.crap.utils.createChangedMap
import cut.the.crap.utils.toBson
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class UserRepository(db: CoroutineDatabase) : Repository<User> {

    override lateinit var mongoCollection: CoroutineCollection<User>

    init {
        mongoCollection = db.getCollection()
    }

    suspend fun updateUser(user: User) : User {
        val oldUser = getById(user._id)
        oldUser?.createChangedMap(user)?.toBson()?.let {
            mongoCollection.updateOne(Model::_id eq user._id,it)
        }
        return user
    }

    suspend fun getFriends(friendList: List<String>) : FriendsResponse {
        val users =  mongoCollection.find(Model::_id `in` friendList).toList()
            val friends = users.map { it.toFriend() }
        return FriendsResponse(friends)
    }

    private fun User.toFriend() : Friend {
        return Friend(
            _id = _id,
            displayName = displayName,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
        )
    }
}

@kotlinx.serialization.Serializable
data class FriendsResponse(
    val friends: List<Friend> = emptyList()
)

@kotlinx.serialization.Serializable
data class Friend(
    override val _id: String = "",
    override var displayName: String = "Susanne",
    override var firstName: String = "Susanne",
    override var lastName: String = "Kosanke",
    override var email: String = "kosnake@gmail.com",
    override var phoneNumber: String = "+491782884137",
    val chatId: String = "",
) : PersonData, Model