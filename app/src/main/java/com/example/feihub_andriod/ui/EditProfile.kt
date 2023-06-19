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
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.data.model.User
import com.example.feihub_andriod.services.S3Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.HashMap;

class EditProfile : AppCompatActivity() {
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
    private lateinit var imageuri: Uri
    private lateinit var profileOrCoverPhoto: String
    val s3Service = S3Service()
    val user = intent.getSerializableExtra("user") as? User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        profilePhotoButton = findViewById(R.id.profilePhotoButton)
        name  = findViewById(R.id.nameEdited)
        paternalSurname = findViewById(R.id.paternalSurnameEdited)
        maternalSurname = findViewById(R.id.maternalSurnameEdited)
        saveChanges = findViewById(R.id.buttonSaveChanges)
        photo = findViewById(R.id.setting_profile_image)
        pd = ProgressDialog(this)
        pd.setCanceledOnTouchOutside(false)
        cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        name.text = Editable.Factory.getInstance().newEditable(user!!.name)
        paternalSurname.text = Editable.Factory.getInstance().newEditable(user!!.paternalSurname)
        maternalSurname.text = Editable.Factory.getInstance().newEditable(user!!.maternalSurname)
        if(user.profilePhoto != null){
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
        profilePhotoButton.setOnClickListener {
            profileOrCoverPhoto = "image"
            showImagePicDialog()
        }

        saveChanges.setOnClickListener {
            pd.setMessage("Guardando cambios")
        }
    }
    private fun editProfile(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val uploadSucess = s3Service.uploadImage(imageuri.toString(), SingletonUser.username!!)
                if(uploadSucess){
                    val imageUrl = s3Service.getImageURL(SingletonUser.username!!)

                }else{
                    Toast.makeText(applicationContext, "No se pudo guardar tu foto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun checkStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return result
    }

    private fun requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST)
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST)
    }

    private fun showImagePicDialog() {
        val options = arrayOf("Camara", "Galleria")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona una imagen desde:")
        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission()
                } else {
                    pickFromCamera()
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }
        builder.create().show()
    }
    private fun pickFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, SingletonUser.username)
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
        imageuri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri)
        startActivityForResult(cameraIntent, IMAGE_PICKCAMERA_REQUEST)
    }

    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST)
    }

}