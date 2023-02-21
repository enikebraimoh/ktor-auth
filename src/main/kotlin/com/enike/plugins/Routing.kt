package com.enike.plugins

import com.enike.data.user.UserDataSource
import com.enike.login.login
import com.enike.security.hashing.HashingService
import com.enike.security.token.TokenConfig
import com.enike.security.token.TokenService
import com.enike.signup.signUpRoute
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {

        signUpRoute(hashingService, userDataSource)
        login(userDataSource, hashingService, tokenService, tokenConfig)
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
