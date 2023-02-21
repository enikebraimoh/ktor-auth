package com.enike.security.token

data class TokenConfig(
    val secret: String,
    val expirationTime: Long,
    val issuer: String,
    val audience: String
)