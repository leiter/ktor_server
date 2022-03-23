package cut.the.crap.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID


fun Route.fileStorage() {

    var fileName = ""

    post("/upload") {

        // retrieve all multipart data (suspending)
        val multipart = call.receiveMultipart()
        multipart.forEachPart { part ->
            // if part is a file (could be form item)
            if(part is PartData.FileItem) {
                // retrieve file name of upload
                fileName = part.originalFileName!!
                val file = File("/home/mandroid/uploads/$fileName")

                // use InputStream from part to save file
                part.streamProvider().use { its ->
                    // copy the stream to  var fileName = ""
                    file.outputStream().buffered().use {
                        // note that this is blocking
                        its.copyTo(it)
                    }
                }
            }
            // make sure to dispose of the part after use to prevent leaks
            part.dispose()
            call.respondText("uploaded to 'uploads/$fileName'")
        }
    }

    get("/{name}") {
        // get filename from request url
        val filename = call.parameters["name"]!!
        // construct reference to file
        // ideally this would use a different filename
        val file = File("/uploads/$filename")
        if(file.exists()) {
            call.respondFile(file)
        }
        else call.respond(HttpStatusCode.NotFound)
    }



}


private fun createUniqueName() : String {
    var name = UUID.randomUUID().toString()


    return name
}


suspend fun ApplicationCall.respondBytesFlow(
    contentType: ContentType? = null,
    status: HttpStatusCode? = null,
    flow: Flow<ByteBuffer>
) {
    respond(FlowWriterContent(flow, contentType, status))
}

private class FlowWriterContent(private val body: Flow<ByteBuffer>,
                                override val contentType: ContentType?,
                                override val status: HttpStatusCode? = null) : OutgoingContent.WriteChannelContent() {
    override suspend fun writeTo(channel: ByteWriteChannel) {
        body.collect {  buffer ->
            channel.writeFully(buffer)
        }
    }
}

//suspend fun Flow<ByteBuffer>.collectAndRespond(call, contentType, status)