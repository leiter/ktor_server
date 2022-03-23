package cut.the.crap.routes

import cut.the.crap.data.ServerErrorMessage.FileUploadFailed
import cut.the.crap.repositories.FileMetaData
import cut.the.crap.repositories.FileMetaDataRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File
import java.util.*


fun Route.fileStorage(fileMetaDataRepository: FileMetaDataRepository) {

    authenticate("auth-jwt") {
        post("/upload") {
            val filename = call.parameters["filename"]!!
            val principal = call.principal<JWTPrincipal>()
            principal?.subject?.let { userId ->
//                val fileMetaData = FileMetaData(displayName = filename, ownerId = userId)
//                val fileId = fileMetaDataRepository.uniqueId(fileMetaData)
//                val saveMetaData = fileMetaData.copy(id = fileId)
//                val file = File("/home/mandroid/uploads/$fileId")
//                val multipart = call.receiveMultipart()
//                multipart.forEachPart { part ->
//                    if (part is PartData.FileItem) {
//                        part.streamProvider().use { its ->
//                            file.outputStream().buffered().use {
//                                // note that this is blocking
//                                its.copyTo(it)
//                            }
//                        }
//                    }
//                    // make sure to dispose of the part after use to prevent leaks
//                    part.dispose()
//                    call.respondText("uploaded to 'uploads/$filename'")
//                }


            } ?: call.respond(HttpStatusCode.Unauthorized, FileUploadFailed)

        }


    }


    get("/{name}") {
        // get filename from request url
        val filename = call.parameters["name"]!!
        // construct reference to file
        // ideally this would use a different filename
        val file = File("/home/mandroid/uploads/$filename")
        if (file.exists()) {
            call.respondFile(file)
        } else call.respond(HttpStatusCode.NotFound)
    }


}

private fun createUniqueName(): String {
    var name = UUID.randomUUID().toString()


    return name
}


//suspend fun ApplicationCall.respondBytesFlow(
//    contentType: ContentType? = null,
//    status: HttpStatusCode? = null,
//    flow: Flow<ByteBuffer>
//) {
//    respond(FlowWriterContent(flow, contentType, status))
//}
//
//private class FlowWriterContent(private val body: Flow<ByteBuffer>,
//                                override val contentType: ContentType?,
//                                override val status: HttpStatusCode? = null) : OutgoingContent.WriteChannelContent() {
//    override suspend fun writeTo(channel: ByteWriteChannel) {
//        body.collect {  buffer ->
//            channel.writeFully(buffer)
//        }
//    }
//}
//suspend fun Flow<ByteBuffer>.collectAndRespond(call, contentType, status)