package cut.the.crap.plugins

import cut.the.crap.chatroom.RoomController
import cut.the.crap.repositories.RefreshTokenRepository
import cut.the.crap.repositories.UserRepository
import cut.the.crap.routes.*
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val roomController by inject<RoomController>()
    val userRepository by inject<UserRepository>()
    val refreshTokenRepository by inject<RefreshTokenRepository>()

    install(Routing) {
        chatSocket(roomController)
        getAllMessages(roomController)
        login(userRepository, refreshTokenRepository)
        register(userRepository, refreshTokenRepository)
        refreshToken(refreshTokenRepository)
        fileStorage()
    }
}
