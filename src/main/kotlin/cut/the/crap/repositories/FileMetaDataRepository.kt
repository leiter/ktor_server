package cut.the.crap.repositories

import cut.the.crap.data.Model
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import kotlinx.serialization.Serializable
import org.litote.kmongo.eq

class FileMetaDataRepository(db: CoroutineDatabase) : Repository<FileMetaData> {
    override lateinit var mongoCollection: CoroutineCollection<FileMetaData>

    init {
        mongoCollection = db.getCollection()
    }

    suspend fun updateOrCreateAvatar(fileMetaData: FileMetaData): Pair<FileMetaData, String> {
        val item = mongoCollection.findOne(FileMetaData::ownerId eq fileMetaData.ownerId)
        val id: String
        if (item == null) {
            id = ""
            add(fileMetaData)
        } else {
            id = item.id
            delete(item.id)
            add(fileMetaData)
        }
        return Pair(fileMetaData, id)
    }

    suspend fun getFileMetaOfAvatar(ownerId: String) : FileMetaData? {
        return mongoCollection.findOne(FileMetaData::ownerId eq ownerId)
    }

}

@Serializable
data class FileMetaData(
    override val id: String = ObjectId().toString(),
    val displayName: String,
    val ownerId: String,
) : Model