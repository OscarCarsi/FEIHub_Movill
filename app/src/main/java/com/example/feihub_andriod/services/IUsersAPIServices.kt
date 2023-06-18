package com.example.feihub_andriod.services
import com.example.feihub_andriod.data.model.*;
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
interface IUsersAPIServices {
    @POST("credentials/login")
    fun getUserCredentials(@Body credentials: HashMap<String, String>): Call<UserCredentials>

    @POST("credentials")
    fun createCredentials(@Body newCredentials: Credentials): Call<Int>

    @POST("users")
    fun createUser(@Body body: Any): Call<Int>
}