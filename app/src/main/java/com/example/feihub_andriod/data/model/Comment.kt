package com.example.feihub_andriod.data.model
import java.io.Serializable
import java.util.Date
data class Comment(
    var commentId: String? = null,
    var author: String? = null,
    var body: String? = null,
    var dateOfComment: Date? = null,
    var _id: String? = null
): Serializable





