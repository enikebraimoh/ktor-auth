package com.enike.login

import com.enike.data.user.UserDataSource
import com.enike.security.hashing.HashingService
import com.enike.security.hashing.SaltedHash
import com.enike.security.token.TokenClaims
import com.enike.security.token.TokenConfig
import com.enike.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.login(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    config: TokenConfig
) {

    post("/login") {
        val request = kotlin.runCatching { call.receiveNullable<LoginRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)

        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "user does not exists")
        } else {
            val isPasswordValid = hashingService.verify(
                password = request.password,
                saltedHash = SaltedHash(hash = user.password, user.salt)
            )

            if (!isPasswordValid) {
                call.respond(HttpStatusCode.Conflict, "incorrect password")
            }


            val token = tokenService.generate(
                config = config,
                TokenClaims(
                    name = "userId",
                    value = user.id.toString()
                )
            )

            call.respond(
                HttpStatusCode.OK,
                LoginResponse(
                    token = token
                )
            )



        }

    }

}