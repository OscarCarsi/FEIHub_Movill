package com.example.feihub_andriod.ui.login

import android.app.Activity
import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Message
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.feihub_andriod.databinding.LoginBinding

import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.services.UsersAPIServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    fun showDialogLogin(title: String,message: String ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar") { dialog, which ->
            dialog.dismiss()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val usersAPIServices = UsersAPIServices()
        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        login.setOnClickListener {
            val enteredUsername = username.text.toString()
            val enteredPassword = password.text.toString()
            lifecycleScope.launch {
                val credentialsUser =
                    usersAPIServices.getUserCredentials(enteredUsername, enteredPassword)
                if (credentialsUser != null) {
                    if (credentialsUser.statusCode == 200) {
                        showDialogLogin("notificación", "Ir a la pantalla principal")
                    }
                } else {
                    showDialogLogin("error", "Los credenciales son inválidos")
                }
            }
        }
    }


    suspend fun getCredentials(username: String, password: String){
        val login = binding.login
        val usersAPIServices = UsersAPIServices()
        login.setOnClickListener {
            lifecycleScope.launch {
                val credentialsUser = usersAPIServices.getUserCredentials(username, password)
                if (credentialsUser != null) {
                    if(credentialsUser.statusCode == 200){
                        showDialogLogin("notificación", "a pantalla principal")
                    }
                }
                else{
                    showDialogLogin("error", "vengo vacío")
                }
            }
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}