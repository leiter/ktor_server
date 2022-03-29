package cut.the.crap.routes

import cut.the.crap.chatroom.RoomController
import cut.the.crap.data.MemberAlreadyExistsException
import cut.the.crap.repositories.MessageDataRepository
import cut.the.crap.session.ChatSession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.chatSocket(roomController: RoomController) {
    // use jwt here
//    authenticate("chat-authorization"){

        webSocket("/chat-socket") {  // maybe use path instead of param

            val session = call.sessions.get<ChatSession>()
            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                return@webSocket
            }

            try {
                roomController.onJoin(
                    username = session.username,
                    sessionId = session.sessionId,
                    socket = this
                )
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        roomController.sendMessage(
                            senderUsername = session.username,
                            message = frame.readText(),
                            sessionId = session.sessionId
                        )
                    }
                }
//                outgoing.send(Frame.Text(createJwtToken(TokenData.ChatAccessToken(session.sessionId),call)))
            } catch (e: MemberAlreadyExistsException) {
                call.respond(HttpStatusCode.Conflict)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                roomController.tryDisconnect(session.username)
            }
        }

//    }

}

fun Route.getNewSessionId(messageDataRepository: MessageDataRepository) {
    get("/newSessionId") {
        val sessionId = messageDataRepository.getChatRoom(call.parameters["sessionId"]).id
        call.respond(sessionId)
    }
}


fun Route.getAllMessages(roomController: RoomController) {
    get("/messages") {
        // param chatId
        val sessionId = call.parameters["sessionId"]
        call.respond(
            HttpStatusCode.OK,
            roomController.getAllMessages(sessionId)  // restrict with chatId and last 20 messages
        )
    }
}

