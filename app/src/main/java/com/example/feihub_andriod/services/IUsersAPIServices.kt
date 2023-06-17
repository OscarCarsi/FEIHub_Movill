package com.example.feihub_andriod.services
import com.example.feihub_andriod.data.model.*;
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
interface IUsersAPIServices {
    @POST("/credentials/login")
    suspend fun getUserCredentials(@Body username: String, password: String): Response<UserCredentials>

    @POST("/credentials")
    suspend fun createCredentials(@Body newCredentials: Credentials): Response<Int>

    @POST("/users")
    suspend fun createUser(@Body body: Any): Response<Int>
}