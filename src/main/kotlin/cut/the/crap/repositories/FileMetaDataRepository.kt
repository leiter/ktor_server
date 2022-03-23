package cut.the.crap.repositories

import cut.the.crap.data.Model
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import kotlinx.serialization.Serializable

class FileMetaDataRepository  (db: CoroutineDatabase) : Repository<FileMetaData> {
    override lateinit var mongoCollection: CoroutineCollection<FileMetaData>

    init {
        mongoCollection = db.getCollection()
    }


}

@Serializable
data class FileMetaData(
    override val id: String = ObjectId().toString(),
    val displayName: String,
    val ownerId: String,
) : Model