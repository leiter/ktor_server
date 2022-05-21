package cut.the.crap.routes

import cut.the.crap.common.ShoutOut
import cut.the.crap.repositories.ShoutOutRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.shoutOut(shoutOutRepository: ShoutOutRepository) {

    authenticate("auth-jwt") {
        get("/shoutout") {
            call.respond(
                HttpStatusCode.OK,
                shoutOutRepository.getAll()  // restrict with chatId and last 20 messages
            )
        }
    }

    authenticate("auth-jwt") {
        post("/shoutout") {
            val shout = call.receive<ShoutOut>()
            call.respond(shoutOutRepository.add(shout))
        }
    }

}