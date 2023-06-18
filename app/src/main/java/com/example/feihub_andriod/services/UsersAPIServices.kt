package com.example.feihub_andriod.services
import android.util.Log
import android.widget.Toast
import com.example.feihub_andriod.data.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class UsersAPIServices {
    private val httpClient: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8083/apiusersfeihub/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getUserCredentials(username: String, password: String): UserCredentials {
        val service = httpClient.create(IUsersAPIServices::class.java)
        var userCredentials = UserCredentials()
        val credentials = HashMap<String, String>()
        credentials["username"] = username
        credentials["password"] = password
        val call = service.getUserCredentials(credentials)
        val response = call.execute()
        if (response.isSuccessful) {
            val userCredentials = response.body()!!
            userCredentials.statusCode = 200
            return userCredentials
        } else {
            val userCredentials = UserCredentials()
            userCredentials.statusCode = 500
            return userCredentials
        }
    }

    fun createCredentials(newCredentials: Credentials): Int {
        val service = httpClient.create(IUsersAPIServices::class.java)
        var responseCode = 0
        try {
            val call = service.createCredentials(newCredentials)
            val response = call.execute()
            if (response.isSuccessful) {
                responseCode = 200

            } else {
                responseCode = 500
            }
            return responseCode
        } catch (ex: IOException) {
            responseCode = 500
            return responseCode
        }
    }

    fun createUser(newUser: User, rol: String): Int {
        val service = httpClient.create(IUsersAPIServices::class.java)
        var responseCode = 0
        val jsonBody = JsonObject().apply {
            addProperty("username", newUser.username)
            addProperty("name", newUser.name)
            addProperty("paternalSurname", newUser.paternalSurname)
            addProperty("maternalSurname", newUser.maternalSurname)
            addProperty("schoolId", newUser.schoolId)
            addProperty("educationalProgram", newUser.educationalProgram)
            addProperty("rol", rol)
        }

        val call = service.createUser(jsonBody)
        val response = call.execute()

        if (response.isSuccessful) {
            responseCode = 200

        } else {
            responseCode = 500
        }
        return responseCode
    }
    fun getUser(username: String): User? {
        val service = httpClient.create(IUsersAPIServices::class.java)
        val call = service.getUser(username)
        val response = call.execute()
        if (response.isSuccessful) {
            val user = response.body()
            if(user != null){
                return user
            }
            else{
                return null
            }
        } else {
            return null
        }
    }
    fun getExistingUser(email: String): String? {
        val service = httpClient.create(IUsersAPIServices::class.java)
        val call = service.getExistingUser(email)
        val response = call.execute()
        if (response.isSuccessful) {
            val credentials = response.body()
            if(credentials != null){
                val email = credentials?.email
                return email
            }
            else{
                return null
            }
        } else {
            return null
        }
    }

    companion object {
        fun create() {
            TODO("Not yet implemented")
        }
    }
    data class CreateUserRequest(val newUser: User, val rol: String)
}
