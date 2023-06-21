package com.example.feihub_andriod.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.Posts
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.data.model.User
import com.example.feihub_andriod.services.PostsAPIServices
import com.example.feihub_andriod.services.UsersAPIServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private lateinit var profilePhoto: ImageView
    private lateinit var name: TextView
    private lateinit var usernameUser: TextView
    private lateinit var schoolId: TextView
    private lateinit var postrecycle: RecyclerView
    private lateinit var editProfileButton: FloatingActionButton
    private lateinit var pd: ProgressDialog
    private var userObtained = User()
    private lateinit var posts: MutableList<Posts>
    var adapterPosts: AdapterPosts? = null
    private val usersAPIServices = UsersAPIServices()
    private val postsAPIServices = PostsAPIServices()
    companion object {
        private const val ARG_USERNAME = "username"

        private var username: String? = null
        fun newInstance(username: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        postrecycle = view.findViewById(R.id.recyclerposts)
        profilePhoto = view.findViewById(R.id.profilePhoto)
        usernameUser = view.findViewById(R.id.usernameUser)
        schoolId = view.findViewById(R.id.schoolIdUser)
        name = view.findViewById(R.id.completeNameUser)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        pd = ProgressDialog(activity)
        pd.setCanceledOnTouchOutside(false)
        username = arguments?.getString(ARG_USERNAME)
        addData()
        loadMyPosts()
        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfile::class.java)
            intent.putExtra("user", userObtained)
            if (userObtained != null) {
                startActivity(intent)
            } else {
                Toast.makeText(context, userObtained.username, Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
    private fun addData(){
        CoroutineScope(Dispatchers.Main).launch {
            userObtained = withContext(Dispatchers.IO) {
                async { usersAPIServices.getUser(username!!) }.await()!!
            }
            usernameUser.text = userObtained.username
            if(userObtained.schoolId!=null){
                schoolId.text = userObtained.schoolId
            }
            if(userObtained.profilePhoto != null){
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.usuario)
                    .error(R.drawable.ic_errorimage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                Glide.with(requireContext())
                    .load(userObtained.profilePhoto)
                    .apply(requestOptions)
                    .into(profilePhoto)
            }
            name.text = "%s %s %s".format(userObtained.name, userObtained.paternalSurname, userObtained.maternalSurname)
            if(userObtained.username != SingletonUser.username){
                editProfileButton.visibility = View.INVISIBLE
            }
        }

    }
    private fun loadMyPosts() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        postrecycle.layoutManager = layoutManager
        CoroutineScope(Dispatchers.Main).launch {
            posts = withContext(Dispatchers.IO) {
                async { postsAPIServices.getPostsByUsername(username!!) }.await()!!
            } as MutableList<Posts>
            if (posts.isNotEmpty()) {
                if(posts[0].statusCode == 200){
                    adapterPosts = AdapterPosts(requireContext(), posts!!)
                    postrecycle.adapter = adapterPosts
                }
                else{
                    Toast.makeText(context, "Error al obtener los posts", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No existen posts recientes", Toast.LENGTH_SHORT).show()
            }
        }

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
}