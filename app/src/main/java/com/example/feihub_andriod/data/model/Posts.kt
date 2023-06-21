package com.example.feihub_andriod.data.model

import java.io.Serializable
import java.util.Date

data class Posts(
    var _id: String? = null,
    var id: String? = null,
    var title: String? = null,
    var author: String? = null,
    var body: String? = null,
    var dateOfPublish: Date? = null,
    var photos: Array<Photo>? = null,
    var target: String? = null,
    var likes: Int? = null,
    var dislikes: Int? = null,
    var reports: Int? = null,
    var comments: MutableList<Comment>? = null,
    var statusCode: Int? = null,
    var __v: Int? = null
): Serializable

