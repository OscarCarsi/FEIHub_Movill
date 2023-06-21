package com.example.feihub_andriod.services
import com.example.feihub_andriod.data.model.*;
import com.example.feihub_andriod.data.model.SingletonUser.token
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
interface IPostsAPIServices {
    @POST("posts/createPost")
    fun createPost(@Body newPost: Posts, @Header("token") token:String): Call<Posts>
    @GET("posts/postsTarget/{target}")
    fun getPrincipalPosts(@Path("target") target: String,  @Header("token") token:String) : Call<List<Posts>>
    @PUT("posts/like/{postId}")
    fun addLike(@Path("postId")postId: String, @Header("token") token:String) : Call<ResponseBody>
    @PUT("posts/removeLike/{postId}")
    fun removeLike(@Path("postId")postId: String, @Header("token") token:String) : Call<ResponseBody>
    @PUT("posts/dislike/{postId}")
    fun addDislike(@Path("postId")postId: String, @Header("token") token:String) : Call<ResponseBody>
    @PUT("posts/removeDislike/{postId}")
    fun removeDislike(@Path("postId")postId: String, @Header("token") token:String) : Call<ResponseBody>
    @PUT("posts/addReport")
    fun addReport(@Body body: JsonObject, @Header("token") token:String) : Call<ResponseBody>
    @POST("posts/addComment")
    fun addComment(@Body body: JsonObject, @Header("token") token:String) : Call<ResponseBody>
    @DELETE("posts/deletePost/{postId}")
    fun deletePost(@Path("postId")postId: String, @Header("token") token:String) : Call<ResponseBody>
    @GET("posts/postsAuthor/{username}")
    fun getPostByUsername(@Path("username") username: String,  @Header("token") token:String) : Call<List<Posts>>
}