package cut.the.crap.repositories

import cut.the.crap.common.User
import cut.the.crap.utils.createChangedMap
import org.junit.Test
import kotlin.test.assertTrue


internal class TestCreateMongoUpdate {

    @Test
    fun testCreateMongoUpdate() {
        val first = User(displayName = "One", email = "gerd@gmail.com", isAnonymous = true)
        val second = User(displayName = "Second", email = "gerd@gmail.de", isAnonymous = false, chatIds = listOf("sdfahkjfs"))
        val result = first.createChangedMap(second)
        assertTrue {  result.keys.contains("displayName") }
        assertTrue {  result.keys.contains("email") }
        assertTrue {  result.keys.contains("isAnonymous") }
        assertTrue {  result.keys.contains("chatIds") }
        assertTrue {  result["displayName"] == "Second" }
    }
}