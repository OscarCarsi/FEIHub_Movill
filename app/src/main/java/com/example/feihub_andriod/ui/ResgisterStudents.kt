package com.example.feihub_andriod.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


class ResgisterStudents : AppCompatActivity() {
    var name: String? = null
    var paternalSurname: String? = null
    var maternalSurname: String? = null
    var schoolId: String? = null
    var educationalProgram: String? = null
    var username: String? = null
    var password: String? = null
    var rol = "STUDENT"
    private val usersAPIServices = UsersAPIServices()
    private val hasher = Hasher()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resgister_students)
        val spinnerEducationalProgram = findViewById<Spinner>(R.id.spinnerEducationalProgram)
        val adapter = ArrayAdapter.createFromResource(this, R.array.educationalPrograms, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEducationalProgram.adapter = adapter
        val buttonSingin = findViewById<Button>(R.id.buttonRegisterStudent)
        val login = findViewById<TextView>(R.id.textLogin)
        buttonSingin.setOnClickListener {
            name = findViewById<EditText>(R.id.nameStudent).text.toString()
            paternalSurname = findViewById<EditText>(R.id.paternalSurnameStudent).text.toString()
            maternalSurname = findViewById<EditText>(R.id.maternalSurnameStudent).text.toString()
            schoolId = findViewById<EditText>(R.id.schoolIdStudent).text.toString()
            educationalProgram = spinnerEducationalProgram.selectedItem as? String
            username = findViewById<EditText>(R.id.usernameStudent).text.toString()
            password = findViewById<EditText>(R.id.passwordStudent).text.toString()
            singin()
        }
        login.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
    fun singin(){
        if(validateNullFieldsStudents()){
            if(validateFieldsStudent()){
                var credentials = Credentials()
                val email = "z" + schoolId!!.substring(0, 1).lowercase() + schoolId!!.substring(1, 9) + "@estudiantes.uv.mx";
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
                user.educationalProgram = educationalProgram
                user.schoolId = schoolId
                CoroutineScope(Dispatchers.Main).launch {

                        val validateExistingUser = withContext(Dispatchers.IO) {
                            async { usersAPIServices.getExistingUser(email) }.await()
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
    fun validateFieldsStudent(): Boolean {
        var correctField = false
        correctField = schoolId!!.length == 9
        correctField = correctField && schoolId!!.startsWith("S")
        correctField = correctField && validateSpecialCharacter(name!!)
        correctField = correctField && validateSpecialCharacter(paternalSurname!!)
        correctField = correctField && validateSpecialCharacter(maternalSurname!!)
        return correctField
    }
    fun validateNullFieldsStudents(): Boolean {
        return !name.isNullOrBlank() && !paternalSurname.isNullOrBlank() && !maternalSurname.isNullOrBlank() && !schoolId.isNullOrBlank() && !password.isNullOrBlank() && !educationalProgram.isNullOrBlank() && !username.isNullOrBlank()
    }

}