package com.example.feihub_andriod.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.Photo
import com.example.feihub_andriod.data.model.Posts
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.services.PostsAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class AddPostFragment : Fragment() {

    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var pd: ProgressDialog
    private lateinit var upload: Button
    private lateinit var spinnerTarget: Spinner
    var newPost = Posts()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

        title = view.findViewById(R.id.textTitle)
        description = view.findViewById(R.id.textDescription)
        upload = view.findViewById(R.id.buttonUpload)
        pd = ProgressDialog(requireContext())
        pd.setCanceledOnTouchOutside(false)
        val intent = activity?.intent
        val postsAPIServices = PostsAPIServices()
        spinnerTarget = view.findViewById(R.id.spinnerTarget)
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.target, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTarget.adapter = adapter
        upload.setOnClickListener {
            val titleText = title.text.toString().trim { it <= ' ' }
            val descriptionText = description.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(titleText)) {
                title.error = "Título vacío"
                Toast.makeText(context, "No puedes dejar vacío el título", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(descriptionText)) {
                description.error = "Descripción vacía"
                Toast.makeText(context, "No puedes dejar vacía la descripción", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            fun getCurrentDate(): Date {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -1) // Restar un día
                return calendar.time
            }
            CoroutineScope(Dispatchers.Main).launch {

                newPost.author = SingletonUser.username!!
                newPost.body = descriptionText
                newPost.title = titleText
                if(spinnerTarget.selectedItem as? String == "Académicos"){
                    newPost.target = "ACADEMIC"
                }
                if(spinnerTarget.selectedItem as? String == "Estudiantes"){
                    newPost.target ="STUDENT"
                }
                if(spinnerTarget.selectedItem as? String == "Todos"){
                    newPost.target = "EVERYBODY"
                }
                newPost.photos = emptyArray()
                newPost.dateOfPublish = getCurrentDate()


                val responseCode = withContext(Dispatchers.IO) {
                    async { postsAPIServices.addPost(newPost) }.await()!!
                }
                if(responseCode == 200){
                    Toast.makeText(
                        context,
                        "Publiacación creada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    Toast.makeText(
                        context,
                        "Error al crear la publicación" +
                                "",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }


        }
        return view
    }
}
