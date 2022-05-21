package cut.the.crap.utils

import com.mongodb.client.model.Updates
import cut.the.crap.Model
import org.bson.conversions.Bson
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

inline fun <reified T : Model> T.createChangedMap(other: T): Map<String, Any> {

    val thisProps = this::class.memberProperties
    val otherProps = other::class.memberProperties

    val map = hashMapOf<String, Any>()
    thisProps.forEach { me ->
        if (me.visibility == KVisibility.PUBLIC) {
            val otherProp = otherProps.first { it.name == me.name }
            if (otherProp.getter.call(other) != me.getter.call(this)) {
                val value = otherProp.getter.call(other)
                value?.let { map[me.name] = value }
            }
        }
    }
    return map
}

fun Map<String, Any>.toBson(): Bson {
    return Updates.combine(
        this.entries.map {
            Updates.push(it.key, it.value)
        }
    )
}