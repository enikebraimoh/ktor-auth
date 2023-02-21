package com.enike.data.user

interface UserDataSource {
    suspend fun getUserByEmail(email: String): User?
    suspend fun createUser(user: User) : Boolean
}