package com.enike.signup

import com.enike.data.user.MongoUserDataSource
import com.enike.data.user.User
import com.enike.data.user.UserDataSource
import com.enike.security.hashing.HashingService
import com.enike.security.token.TokenService
import com.enike.signup.models.SignUpRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.signUpRoute(
    hashingService: HashingService,
    userDataSource: UserDataSource,
) {
    post("/signup") {
        val request = kotlin.runCatching { call.receiveNullable<SignUpRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.email.isBlank() || request.name.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 6

        if (areFieldsBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }


        val saltedHash = hashingService.generateSaltedHash(request.password)

        val user = User(
            email = request.email,
            name = request.name,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )

        val wasAcknowledged = userDataSource.createUser(user)

        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }


    }
}