package com.enike.login

import com.enike.data.user.UserDataSource
import com.enike.login.authenticate
import com.enike.security.hashing.HashingService
import com.enike.security.hashing.SaltedHash
import com.enike.security.token.TokenClaims
import com.enike.security.token.TokenConfig
import com.enike.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.login(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    config: TokenConfig
) {

    post("login") {
        val request = kotlin.runCatching { call.receiveNullable<LoginRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)

        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "user does not exists")
            return@post
        } else {

            val isPasswordValid = hashingService.verify(
                password = request.password,
                saltedHash = SaltedHash(
                    hash = user.password,
                    salt = user.salt
                )
            )

            if (!isPasswordValid) {
                call.respond(HttpStatusCode.Conflict, "incorrect password")
                return@post
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
            return@post

        }

    }

}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}