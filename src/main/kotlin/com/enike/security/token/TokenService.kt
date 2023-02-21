package com.enike.security.token

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaims
    ) : String
}