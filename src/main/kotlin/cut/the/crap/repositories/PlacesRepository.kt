package cut.the.crap.repositories

import cut.the.crap.common.Place
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class PlacesRepository(
    db: CoroutineDatabase

) : Repository<Place> {

    override lateinit var mongoCollection: CoroutineCollection<Place>

    init {
        mongoCollection = db.getCollection()
    }

    suspend fun findPlaces(): List<Place> {
        return try {

            mongoCollection.find("").toList()
        } catch (e : Exception) {
            emptyList()
        }
    }

}