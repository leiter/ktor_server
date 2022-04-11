package cut.the.crap.repositories

import cut.the.crap.data.Model
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import kotlinx.serialization.Serializable
import org.litote.kmongo.eq

class FileMetaDataRepository  (db: CoroutineDatabase) : Repository<FileMetaData> {
    override lateinit var mongoCollection: CoroutineCollection<FileMetaData>

    init {
        mongoCollection = db.getCollection()
    }

    suspend fun updateOrCreateAvatar(fileMetaData: FileMetaData) : FileMetaData {
        val item = mongoCollection.findOne(FileMetaData::ownerId eq fileMetaData.ownerId)
        if(item==null) {
            add(fileMetaData)
        } else {
            delete(item.id)
            add(fileMetaData)
        }
        return fileMetaData
    }



}

@Serializable
data class FileMetaData(
    override val id: String = ObjectId().toString(),
    val displayName: String,
    val ownerId: String,
) : Model