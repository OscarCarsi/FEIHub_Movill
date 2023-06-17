package com.example.feihub_andriod.data.model

data class User(
    val username: String,
    val name: String,
    val paternalSurname: String,
    val maternalSurname: String,
    val schoolId: String,
    val educationalProgram: String,
    val profilePhoto: String?,
    val statusCode: Int
)
