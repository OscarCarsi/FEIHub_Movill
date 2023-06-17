package com.example.feihub_andriod.services
import com.example.feihub_andriod.data.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class UsersAPIServices {
    private val httpClient: Retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8083/apiusersfeihub")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    suspend fun getUserCredentials(username: String, password: String): UserCredentials? {
        val service = httpClient.create(IUsersAPIServices::class.java)
        try {
            val response = service.getUserCredentials(username, password)
            val userCredentials = response.body()
            if (userCredentials != null) {
                userCredentials.statusCode = response.code()
            }
            return userCredentials
        } catch (ex: IOException) {
            val userCredentials = UserCredentials()
            userCredentials.username = null
            userCredentials.statusCode = 500
            return userCredentials
        }
    }

    suspend fun createCredentials(newCredentials: Credentials): Int {
        val service = httpClient.create(IUsersAPIServices::class.java)
        val requestData = newCredentials
        try {
            val response = service.createCredentials(requestData)
            return response.code()
        } catch (ex: IOException) {
            return 500
        }
    }

    suspend fun createUser(newUser: User, rol: Int): Int {
        val service = httpClient.create(IUsersAPIServices::class.java)
        val body = object {
            val newUser = newUser
            val rol = rol
        }
        try {
            val response = service.createUser(body)
            return response.code()
        } catch (ex: IOException) {
            return 500
        }
    }
}
