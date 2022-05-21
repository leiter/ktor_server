package cut.the.crap.routes

import cut.the.crap.common.Place
import cut.the.crap.repositories.PlacesRepository
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.placesRoutes(
    placesRepository: PlacesRepository
) {

    post("/places") {
        val place =  call.receive<Place>()
        placesRepository.add(place)
    }

    get("/places") {
        val places = placesRepository.getAll()
        call.respond(places)
    }


    get("/closePlaces") {

    }



}