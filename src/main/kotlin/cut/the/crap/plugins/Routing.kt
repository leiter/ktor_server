package cut.the.crap.plugins

import cut.the.crap.chatroom.RoomController
import cut.the.crap.repositories.*
import cut.the.crap.routes.*
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val roomController by inject<RoomController>()
    val internalUserRepository by inject<InternalUserRepository>()
    val userRepository by inject<UserRepository>()
    val refreshTokenRepository by inject<RefreshTokenRepository>()
    val fileMetaDataRepository by inject<FileMetaDataRepository>()
    val messageDataRepository by inject<MessageDataRepository>()

    install(Routing) {
        chatSocket(roomController)
        getAllMessages(roomController)
        socketFactory(internalUserRepository)
        getNewSessionId(messageDataRepository)
        login(internalUserRepository, refreshTokenRepository)
        register(userRepository,internalUserRepository, refreshTokenRepository)
        refreshToken(refreshTokenRepository)
        fileStorage(fileMetaDataRepository)
    }
}
