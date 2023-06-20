package com.example.feihub_andriod.services
import com.example.feihub_andriod.data.model.*;
import com.example.feihub_andriod.data.model.SingletonUser.token
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
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
    @PUT("users/{username}")
    fun editUser(@Path("username") username: String, @Body newUser: User, @Header("token") token:String) : Call<List<Int>>
    @GET("users/findUsers/{username}")
    fun findUsers(@Path("username") username:String, @Header("token") token:String) : Call<List<User>>
}