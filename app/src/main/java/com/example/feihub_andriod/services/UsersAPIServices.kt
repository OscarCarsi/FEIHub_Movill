package com.example.feihub_andriod.services
import android.util.Log
import android.widget.Toast
import com.example.feihub_andriod.data.model.*
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
            call.enqueue(object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: Call<Int>,
                    response: Response<Int>
                ) {
                    if (response.isSuccessful) {
                        responseCode = response.code()

                    } else {
                        responseCode = 500
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    responseCode = 500
                }
            })
            return responseCode
        } catch (ex: IOException) {
            responseCode = 500
            return responseCode
        }
    }

    fun createUser(newUser: User, rol: Int): Int {
        val service = httpClient.create(IUsersAPIServices::class.java)
        var responseCode = 0
        val body = object {
            val newUser = newUser
            val rol = rol
        }
        try {
            val call = service.createUser(body)
            call.enqueue(object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: Call<Int>,
                    response: Response<Int>
                ) {
                    if (response.isSuccessful) {
                        responseCode = response.code()

                    } else {
                        responseCode = 500
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    responseCode = 500
                }
            })
            return responseCode
        } catch (ex: IOException) {
            responseCode = 500
            return responseCode
        }
    }

    companion object {
        fun create() {
            TODO("Not yet implemented")
        }
    }
}
