package com.example.feihub_andriod.data.model

object SingletonUser {
    var username: String? = null
    var rol: String? = null
    var token: String? = null

    fun borrarSingleton() {
        username = ""
        rol = ""
        token = ""
    }
}
