package com.example.feihub_andriod.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import android.window.SplashScreen
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.Posts
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.services.PostsAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {
    private lateinit var myuid: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var posts: MutableList<Posts>
    private lateinit var adapterPosts: AdapterPosts
    val postsAPIServices = PostsAPIServices()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.postrecyclerview)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        posts = ArrayList()
        loadPosts()
        return view
    }


    private fun loadPosts() {
        CoroutineScope(Dispatchers.Main).launch {
            posts = withContext(Dispatchers.IO) {
                async { postsAPIServices.getPrincipalPosts(SingletonUser.rol!!) }.await()!!
            } as MutableList<Posts>
            if (posts.isNotEmpty()) {
                if(posts[0].statusCode == 200){
                    adapterPosts = AdapterPosts(requireActivity(), posts)
                    recyclerView.adapter = adapterPosts
                }
                else{
                    Toast.makeText(context, "Error al obtener los posts", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No existen posts recientes", Toast.LENGTH_SHORT).show()
            }

        }


    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView
        searchView.visibility = View.INVISIBLE
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                SingletonUser.borrarSingleton()
                startActivity(Intent(context, SplashScreen::class.java))
                activity?.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
}