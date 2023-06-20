package com.example.feihub_andriod.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.Hasher
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.services.UsersAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Login : AppCompatActivity() {
    private val usersAPIServices = UsersAPIServices()
    private val hasher = Hasher()
    var user: SingletonUser = SingletonUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnLogin = findViewById<Button>(R.id.login)
        val singinStudent = findViewById<TextView>(R.id.registerStudent)
        val singinAcademic = findViewById<TextView>(R.id.registerAcademic)
        btnLogin.setOnClickListener {
            login()
        }
        singinStudent.setOnClickListener{
            val intent = Intent(this, com.example.feihub_andriod.ui.ResgisterStudents::class.java)
            startActivity(intent)
        }
        singinAcademic.setOnClickListener{
            val intent = Intent(this, com.example.feihub_andriod.ui.RegisterAcademics::class.java)
            startActivity(intent)
        }
    }

    private fun login(){
        val username = findViewById<EditText>(R.id.username).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        var hashedPassword = hasher.hash(password)
        val notNullFields = validateNullFields(username, password)
        if(notNullFields){
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val userCredentials = withContext(Dispatchers.IO) {
                        usersAPIServices.getUserCredentials(username, hashedPassword)
                    }
                    if (userCredentials.statusCode == 200) {
                        Toast.makeText(applicationContext, "Bienvenido $username", Toast.LENGTH_SHORT).show()
                        user.username = userCredentials.username;
                        user.rol = userCredentials.rol;
                        user.token = userCredentials.token;
                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        startActivity(intent)
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

