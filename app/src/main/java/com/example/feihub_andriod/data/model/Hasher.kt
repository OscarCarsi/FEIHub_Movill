package com.example.feihub_andriod.data.model

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class Hasher {
    fun hash(password: String): String {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val bytes = sha256.digest(password.toByteArray(StandardCharsets.UTF_8))
        val encryptedPassword = StringBuilder()
        for (byte in bytes) {
            encryptedPassword.append("%02x".format(byte))
        }
        return encryptedPassword.toString()
    }
}