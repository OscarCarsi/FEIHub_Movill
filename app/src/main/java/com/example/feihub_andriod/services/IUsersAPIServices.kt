package com.example.feihub_andriod.services
import com.example.feihub_andriod.data.model.*;
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IUsersAPIServices {

    @POST("credentials/login")
    fun getUserCredentials(@Body credentials: HashMap<String, String>): Call<UserCredentials>

    @POST("credentials")
    fun createCredentials(@Body newCredentials: Credentials): Call<Credentials>

    @POST("users")
    fun createUser(@Body body: JsonObject): Call<User>
    @GET("users/{username}")
    fun getUser(@Path("username") usernanme: String) : Call<User>
    @GET("credentials/{email}")
    fun getExistingUser(@Path("email") email: String) : Call<Credentials>
}