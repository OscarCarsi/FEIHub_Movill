package com.example.feihub_andriod.data.model
import java.io.Serializable
data class User(
    var username: String? = null,
    var name: String? = null,
    var paternalSurname: String? = null,
    var maternalSurname: String? = null,
    var schoolId: String? = null,
    var educationalProgram: String? = null,
    var profilePhoto: String? = null,
    var statusCode: Int? = null
): Serializable
