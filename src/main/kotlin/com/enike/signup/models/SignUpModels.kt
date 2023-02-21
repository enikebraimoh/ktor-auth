package com.enike.signup.models

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val name: String,
    val password: String
)

@Serializable
data class SignUpResponse(
    val token : String
)
