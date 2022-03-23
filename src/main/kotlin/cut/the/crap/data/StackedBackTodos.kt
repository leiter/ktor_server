package cut.the.crap.data

//
//// Trying to use ObjectId from mongo db
//
//@Serializable
//data class TestObjectId(
//    @Serializable(with = MongoObjectIdSerializer::class)
//    @SerialName("_id")
//    val mongoId: ObjectId
//    )
//
//
//@OptIn(ExperimentalSerializationApi::class)
//@Serializer(forClass = ObjectId::class)
//object MongoObjectIdSerializer : KSerializer<ObjectId> {
//
//    override val descriptor = SerialDescriptor("date", ObjectIdDescriptor())
//
//    override fun deserialize(decoder: Decoder): ObjectId {
//        decoder.decodeStructure(descriptor) {
//
//        }
//        return dateFormat.parse(decoder.decodeString())
//    }
//
//    override fun serialize(encoder: Encoder, value: ObjectId) {
//        encoder.encodeString(value.format(dateFormat))
//    }
//}
//
//class ObjectIdDescriptor : SerialDescriptor {
//    // intField is deliberately ignored by serializer -- not present in the descriptor as well
////    element<Long>("_longField") // longField is named as _longField
////    element("stringField", listDescriptor<String>())
//    @ExperimentalSerializationApi
//    override val elementsCount: Int
//        get() = TODO("Not yet implemented")
//
//    @ExperimentalSerializationApi
//    override val kind: SerialKind
//        get() = TODO("Not yet implemented")
//
//    @ExperimentalSerializationApi
//    override val serialName: String
//        get() = TODO("Not yet implemented")
//
//    @ExperimentalSerializationApi
//    override fun getElementAnnotations(index: Int): List<Annotation> {
//        TODO("Not yet implemented")
//    }
//
//    @ExperimentalSerializationApi
//    override fun getElementDescriptor(index: Int): SerialDescriptor {
//        TODO("Not yet implemented")
//    }
//
//    @ExperimentalSerializationApi
//    override fun getElementIndex(name: String): Int {
//        TODO("Not yet implemented")
//    }
//
//    @ExperimentalSerializationApi
//    override fun getElementName(index: Int): String {
//        TODO("Not yet implemented")
//    }
//
//    @ExperimentalSerializationApi
//    override fun isElementOptional(index: Int): Boolean {
//        TODO("Not yet implemented")
//    }
//}