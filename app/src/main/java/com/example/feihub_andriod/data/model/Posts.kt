package com.example.feihub_andriod.data.model

import java.util.Date

data class Posts(
    var _id: String,
    var id: String,
    var title: String,
    var author: String,
    var body: String,
    var dateOfPublish: Date,
    var photos: Array<Photo>,
    var target: String,
    var likes: Int,
    var dislikes: Int,
    var reports: Int,
    var comments: Array<Comment>,
    var statusCode: Int,
    var __v: Int
)

