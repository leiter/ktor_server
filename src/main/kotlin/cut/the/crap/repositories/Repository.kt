package cut.the.crap.repositories

import cut.the.crap.Model
import cut.the.crap.data.PropertyNotFoundException
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

interface Repository<T : Model> {

    var mongoCollection: CoroutineCollection<T>

    suspend fun getById(id: String): T? {
        return try {
            mongoCollection.findOne(Model::_id eq id) //?: throw Exception("No item with that ID exists")
        } catch (t: Throwable) {
            null
            //throw PropertyNotFoundException("Cannot find item")
        }
    }

    suspend fun getAll(): List<T> {
        return try {
            val result = mongoCollection.find()
            result.toList() //asIterable().map { it }
        } catch (t: Throwable) {
            throw Exception("Cannot get all items")
        }
    }

    suspend fun delete(id: String): Boolean {
        return try {
            mongoCollection.findOneAndDelete(Model::_id eq id) ?: throw Exception("No item with that Id exists")
            true
        } catch (t: Throwable) {
            // Log here and find a better solution for setOrUpdate refreshToken
            // throw Exception("Cannot delete item or item not found")
            return false
        }
    }

    suspend fun add(entry: T): T {
        return try {
            mongoCollection.insertOne(entry)
            entry
        } catch (t: Throwable) {
            throw Exception("Cannot add item")
        }
    }

    suspend fun update(entry: Model) : T {
        return try {
            mongoCollection.updateOneById(Model::_id eq entry._id, entry,)
//            mongoCollection.updateOneById(Filters.eq("_id", entry.id), entry)
            mongoCollection.findOne(Model::_id eq entry._id) ?: throw PropertyNotFoundException("No item with that id exists")

        }catch (t: Throwable){
            throw Exception("Cannot update item")
        }
    }

//    suspend fun uniqueId(entry: T): String {
//        var newId = entry.id
//        while (mongoCollection.findOne(Model::_id eq newId) != null){
//            newId = ObjectId().toString()
//        }
//        return newId
//    }

}