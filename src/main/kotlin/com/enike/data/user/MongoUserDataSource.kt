package com.enike.data.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserDataSource(
    private val dataBase: CoroutineDatabase
) : UserDataSource {

    private val userCollection = dataBase.getCollection<User>()

    override suspend fun getUserByEmail(email: String): User? {
        return userCollection.findOne(User::email eq email)
    }

    override suspend fun createUser(user: User): Boolean {
        return userCollection.insertOne(user).wasAcknowledged()
    }
}