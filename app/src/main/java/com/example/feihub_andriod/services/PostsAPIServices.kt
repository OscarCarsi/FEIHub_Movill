package com.example.feihub_andriod.services

import android.util.Log
import com.example.feihub_andriod.data.model.Posts
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.data.model.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostsAPIServices {
    private val CODE_SUCESSFUL = 200
    private val CODE_SERVER_INTERNAL_ERROR = 500
    private val httpClient: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8083/apipostsfeihub/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun addPost(newPost: Posts): Int {
        val service = httpClient.create(IPostsAPIServices::class.java)

        try{
            val call = service.createPost(newPost, SingletonUser.token!!)
            val response = call.execute()
            return if (response.isSuccessful) {
                CODE_SUCESSFUL
            } else {
                CODE_SERVER_INTERNAL_ERROR
            }
        }catch (E: Exception){
            return CODE_SERVER_INTERNAL_ERROR
        }

    }
    fun getPrincipalPosts(target: String): List<Posts> {
        val service = httpClient.create(IPostsAPIServices::class.java)

        val call = service.getPrincipalPosts(target, SingletonUser.token!!)
        val response = call.execute()
        val posts: List<Posts> = if (response.isSuccessful) {
            val postsList = response.body() ?: emptyList()
            if (postsList.isNotEmpty()) {
                postsList[0].statusCode = 200
            }
            postsList
        } else {
            val errorPost = Posts()
            errorPost.statusCode = 500
            listOf(errorPost)
        }
        return posts
    }



}