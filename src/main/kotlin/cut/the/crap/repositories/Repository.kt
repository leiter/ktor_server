package cut.the.crap.repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import cut.the.crap.data.Model
import cut.the.crap.data.PropertyNotFoundException
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

interface Repository<T : Model> {

    var mongoCollection: CoroutineCollection<T>

    suspend fun getById(id: String): T? {
        return try {
            mongoCollection.findOne(Model::id eq id) //?: throw Exception("No item with that ID exists")
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
            mongoCollection.findOneAndDelete(Model::id eq id) ?: throw Exception("No item with that Id exists")
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
            mongoCollection.updateOneById(Model::id eq entry.id, entry)
//            mongoCollection.updateOneById(Filters.eq("_id", entry.id), entry)
            mongoCollection.findOne(Model::id eq entry.id) ?: throw PropertyNotFoundException("No item with that id exists")
        }catch (t: Throwable){
            throw Exception("Cannot update item")
        }
    }

//    suspend fun uniqueId(entry: T): String {
//        var newId = entry.id
//        while (mongoCollection.findOne(Model::id eq newId) != null){
//            newId = ObjectId().toString()
//        }
//        return newId
//    }

}