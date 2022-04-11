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


private const val FILE_PATH = "/home/mandroid/uploads/"

fun Route.fileStorage(fileMetaDataRepository: FileMetaDataRepository) {

    authenticate("auth-jwt") {
        post("/upload") {
//            val filename = call.parameters["filename"]!!
//            val fileContext = call.parameters["fileContext"]!!  // avatar, sharable image, ...
            val principal = call.principal<JWTPrincipal>()
            principal?.subject?.let { userId ->
                val fileMetaData = FileMetaData(displayName = "filename", ownerId = "userId")
                val savedMetaData = fileMetaDataRepository.updateOrCreateAvatar(fileMetaData)

                if (savedMetaData.id != fileMetaData.id) {
                    val deleteMe = File("$FILE_PATH${fileMetaData.id}")
                    deleteMe.delete()
                }
                val file = File("$FILE_PATH${savedMetaData.id}")
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        part.streamProvider().use { its ->
                            file.outputStream().buffered().use {
                                // note that this is blocking
                                its.copyTo(it)
                            }
                        }
                    }
                    // make sure to dispose of the part after use to prevent leaks
                    part.dispose()
                }
                call.respond(savedMetaData)
            } ?: call.respond(HttpStatusCode.Unauthorized, FileUploadFailed)
        }
    }


    get("/{name}") {
        // get filename from request url
        val filename = call.parameters["name"]!!
        // construct reference to file
        // ideally this would use a different filename
        val file = File("$FILE_PATH$filename")
        if (file.exists()) {
            call.respondFile(file)
        } else call.respond(HttpStatusCode.NotFound)
    }


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

