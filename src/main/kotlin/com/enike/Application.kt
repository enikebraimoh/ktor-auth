package com.enike

import com.enike.data.user.MongoUserDataSource
import com.enike.data.user.User
import io.ktor.server.application.*
import com.enike.plugins.*
import com.enike.security.hashing.SHA256HashingService
import com.enike.security.token.JwtTokenService
import com.enike.security.token.TokenConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    val mongoPW = System.getenv("MONGO_PW")
    val dbName = "ktor-auth"

    val dataBase = KMongo.createClient(
        connectionString = "mongodb+srv://richardbraimoh:$mongoPW@cluster0.z3lfqdh.mongodb.net/?retryWrites=true&w=majority"
    ).coroutine.getDatabase(dbName)


    val userDataSource = MongoUserDataSource(dataBase)
    val tokenService = JwtTokenService()

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expirationTime = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )


    configureSecurity(tokenConfig)
    configureSerialization()

    configureMonitoring()

    configureRouting(
        userDataSource = userDataSource,
        tokenService = tokenService,
        tokenConfig = tokenConfig,
        hashingService = SHA256HashingService()
    )
}
