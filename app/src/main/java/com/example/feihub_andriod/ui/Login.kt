package com.example.feihub_andriod.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.example.feihub_andriod.R
import com.example.feihub_andriod.R.id.login
import com.example.feihub_andriod.data.model.Hasher
import com.example.feihub_andriod.services.IUsersAPIServices
import com.example.feihub_andriod.services.UsersAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {
    private val usersAPIServices = UsersAPIServices()
    private val hasher = Hasher()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnLogin = findViewById<Button>(R.id.login)
        val signup = findViewById<AppCompatTextView>(R.id.Singin)
        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login(){
        val username = findViewById<EditText>(R.id.username).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        var hashedPassword = hasher.hash(password)
        val notNullFields = validateNullFields(username, password)
        Log.d("TAG", notNullFields.toString())
        if(notNullFields){
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val userCredentials = withContext(Dispatchers.IO) {
                        usersAPIServices.getUserCredentials(username, hashedPassword)
                    }
                    if (userCredentials.statusCode == 200) {
                        Toast.makeText(applicationContext, "Bienvenido $username", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Verifica tus credenciales", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(applicationContext, "No puedes dejar campos vacíos", Toast.LENGTH_SHORT).show()
        }


    }
    private fun validateNullFields(username: String, password: String ): Boolean{
        var notNull : Boolean = false;
        notNull = username.isNotBlank()
        notNull = notNull && password.isNotBlank()
        return notNull
    }
}

