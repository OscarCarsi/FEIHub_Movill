package com.example.feihub_andriod.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.Credentials
import com.example.feihub_andriod.data.model.Hasher
import com.example.feihub_andriod.data.model.User
import com.example.feihub_andriod.services.UsersAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterAcademics : AppCompatActivity() {
    var name: String? = null
    var paternalSurname: String? = null
    var maternalSurname: String? = null
    var email: String? = null
    var username: String? = null
    var password: String? = null
    var rol = "ACADEMIC"
    private val usersAPIServices = UsersAPIServices()
    private val hasher = Hasher()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_academics)
        val buttonSingin = findViewById<Button>(R.id.buttonRegisterAcademic)
        val login = findViewById<TextView>(R.id.textLogin)
        buttonSingin.setOnClickListener {
            name = findViewById<EditText>(R.id.nameAcademic).text.toString()
            paternalSurname = findViewById<EditText>(R.id.paternalSurnameAcademic).text.toString()
            maternalSurname = findViewById<EditText>(R.id.maternalSurnameAcademic).text.toString()
            username = findViewById<EditText>(R.id.usernameAcademic).text.toString()
            password = findViewById<EditText>(R.id.passwordAcademic).text.toString()
            email = findViewById<EditText>(R.id.emailAcademic).text.toString()
            singin()
        }
        login.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
    fun singin(){
        if(validateNullFieldsAcademic()){
            if(validateFieldsAcademic()){
                var credentials = Credentials()
                credentials.username = username;
                val hashedPassword = hasher.hash(password!!)
                credentials.password = password;
                credentials.email = email;
                credentials.rol = rol;
                val user = User()
                user.username = username
                user.name = name
                user.paternalSurname = paternalSurname
                user.maternalSurname = maternalSurname
                CoroutineScope(Dispatchers.Main).launch {

                    val validateExistingUser = withContext(Dispatchers.IO) {
                        async { usersAPIServices.getExistingUser(email!!) }.await()
                    }
                    if(validateExistingUser != email){
                        val validateUsername = withContext(Dispatchers.IO){
                            async { usersAPIServices.getUser(username!!) }.await()
                        }
                        if(validateUsername == null){
                            val statusCodeCredentials = withContext(Dispatchers.IO){
                                async { usersAPIServices.createCredentials(credentials) }.await()

                            }
                            if(statusCodeCredentials == 200){
                                val statusCodeUser = withContext(Dispatchers.IO){
                                    async { usersAPIServices.createUser(user, rol) }.await()

                                }
                                if(statusCodeUser == 200){
                                    Toast.makeText(applicationContext, "Usuario creado exitosamente, inicia sesión para ingresar", Toast.LENGTH_LONG).show()
                                }
                                else{
                                    Toast.makeText(applicationContext, "No se pudo crear tu usuario, inténtalo más tarde", Toast.LENGTH_LONG).show()
                                }
                            }else{
                                Toast.makeText(applicationContext, "No se pudieron crear tus credenciales, inténtalo más tarde", Toast.LENGTH_LONG).show()
                            }
                        }else{
                            Toast.makeText(applicationContext, "Nombre de usuario en uso, ingresa otro", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(applicationContext, "Ya tienes una cuenta, ve al inicio de sesión e ingresa tus credenciles", Toast.LENGTH_LONG).show()
                    }

                }
            }else{
                Toast.makeText(applicationContext, "Verifica los datos proporcionados", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(applicationContext, "No puedes dejar campos vacíos", Toast.LENGTH_LONG).show()
        }
    }

    fun validateSpecialCharacter(verify: String): Boolean {
        var withoutSpecialCharacter = true
        val specialCharacters = "*#+-_;.@%&/()=!?¿¡{}[]^<>"
        for (character in verify) {
            if (character in '0'..'9') {
                withoutSpecialCharacter = false
            }
        }
        for (character in verify) {
            for (specialCharacter in specialCharacters) {
                if (character == specialCharacter) {
                    withoutSpecialCharacter = true
                }
            }
            if (withoutSpecialCharacter) {
                break
            }
        }
        return withoutSpecialCharacter
    }
    fun validateFieldsAcademic(): Boolean {
        var correctField = false
        correctField = validateEmail(email!!)
        correctField = correctField && validateSpecialCharacter(name!!)
        correctField = correctField && validateSpecialCharacter(paternalSurname!!)
        correctField = correctField && validateSpecialCharacter(maternalSurname!!)
        correctField = correctField && email!!.endsWith("@uv.mx", ignoreCase = true)
        return correctField
    }

    fun validateNullFieldsAcademic(): Boolean {
        return !name.isNullOrBlank() && !paternalSurname.isNullOrBlank() && !maternalSurname.isNullOrBlank() && !email.isNullOrBlank() && !password.isNullOrBlank() && !username.isNullOrBlank()
    }
    fun validateEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return emailRegex.matches(email)
    }

}