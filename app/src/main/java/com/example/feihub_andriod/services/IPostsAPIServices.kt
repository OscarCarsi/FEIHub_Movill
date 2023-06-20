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
interface IPostsAPIServices {
    @POST("posts/createPost")
    fun createPost(@Body newPost: Posts, @Header("token") token:String): Call<Posts>
    @GET("posts/postsTarget/{target}")
    fun getPrincipalPosts(@Path("target") target: String,  @Header("token") token:String) : Call<List<Posts>>
}