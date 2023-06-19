package com.example.feihub_andriod.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.feihub_andriod.R
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable
import android.text.TextUtils;
import android.util.Log
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.contract.ActivityResultContracts

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.data.model.User
import com.example.feihub_andriod.services.S3Service
import com.example.feihub_andriod.services.UsersAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.HashMap;

class EditProfile : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private val storagepath = "Users_Profile_Cover_image/"
    private lateinit var uid: String
    private lateinit var photo: ImageView
    private lateinit var profilePhotoButton: TextView
    private lateinit var name: EditText
    private lateinit var paternalSurname: EditText
    private lateinit var maternalSurname: EditText
    private lateinit var saveChanges: Button
    private lateinit var pd: ProgressDialog
    private val CAMERA_REQUEST = 100
    private val STORAGE_REQUEST = 200
    private val IMAGEPICK_GALLERY_REQUEST = 300
    private val IMAGE_PICKCAMERA_REQUEST = 400
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    private var imageuri: Uri? = null
    private lateinit var profileOrCoverPhoto: String
    private val permissionRequestCode = 123
    private val imagePickRequestCode = 456
    private val REQUEST_PERMISSION = 1
    private val REQUEST_IMAGE_PICK = 2
    val s3Service = S3Service()
    val usersAPIServices = UsersAPIServices()
    var newUser = User()
    var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        name = findViewById(R.id.nameEdited)
        paternalSurname = findViewById(R.id.paternalSurnameEdited)
        maternalSurname = findViewById(R.id.maternalSurnameEdited)
        saveChanges = findViewById(R.id.buttonSaveChanges)
        pd = ProgressDialog(this)
        pd.setCanceledOnTouchOutside(false)
        cameraPermission =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val user = intent.getSerializableExtra("user") as? User
        newUser = user!!
        name.text = Editable.Factory.getInstance().newEditable(user!!.name)
        paternalSurname.text = Editable.Factory.getInstance().newEditable(user!!.paternalSurname)
        maternalSurname.text = Editable.Factory.getInstance().newEditable(user!!.maternalSurname)
        if (user.profilePhoto != null) {
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.usuario)
                .error(R.drawable.ic_errorimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
            Glide.with(this)
                .load(user.profilePhoto)
                .apply(requestOptions)
                .into(photo)
        }

        saveChanges.setOnClickListener {
            pd.setMessage("Guardando cambios")
            editProfile()
        }
    }

    private fun editProfile() {
        if (validateNullFields()) {
            CoroutineScope(Dispatchers.Main).launch {
            try {
                newUser.name = name.text.toString()
                newUser.paternalSurname = paternalSurname.text.toString()
                newUser.maternalSurname = maternalSurname.text.toString()
                withContext(Dispatchers.IO) {
                    val responseCode = usersAPIServices.editUser(newUser)
                    if (responseCode == 200) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                "Perfil editado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                "No se pudo editar tu perfil, inténtalo más tarde",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Error de conexión", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        } else {
            Toast.makeText(
                applicationContext,
                "No se pueden dejar campos vacíos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validateNullFields(): Boolean {
        return !name.text.isNullOrBlank() && !paternalSurname.text.isNullOrBlank() && !maternalSurname.text.isNullOrBlank()
    }


}
