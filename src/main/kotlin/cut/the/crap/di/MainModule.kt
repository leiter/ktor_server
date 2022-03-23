package cut.the.crap.di

import cut.the.crap.chatroom.RoomController
import cut.the.crap.repositories.FileMetaDataRepository
import cut.the.crap.repositories.MessageDataRepository
import cut.the.crap.repositories.RefreshTokenRepository
import cut.the.crap.repositories.UserRepository
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase("stage_database")
    }
    single {
        MessageDataRepository(get())
    }
    single {
        RoomController(get())
    }
    single {
        UserRepository(get())
    }
    single {
        RefreshTokenRepository(get())
    }
    single {
        FileMetaDataRepository(get())
    }
}