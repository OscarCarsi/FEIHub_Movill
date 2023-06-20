package com.example.feihub_andriod.ui
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.Posts
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.data.model.User
import com.example.feihub_andriod.services.UsersAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterUsers: AdapterUsers
    private lateinit var usersList: MutableList<User>
    private val usersAPIServices = UsersAPIServices()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        recyclerView = view.findViewById(R.id.recyclep)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        usersList = mutableListOf()
        return view
    }



    private fun searchUsers(search: String) {
        CoroutineScope(Dispatchers.Main).launch {
            usersList = withContext(Dispatchers.IO) {
                async { usersAPIServices.findUsers(search) }.await()!!
            } as MutableList<User>
            if (usersList.isNotEmpty()) {
                if (usersList[0].statusCode == 200) {
                    adapterUsers = AdapterUsers(requireActivity(), usersList)
                    recyclerView.adapter = adapterUsers
                } else {
                    Toast.makeText(context, "Error al obtener los usuarios", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No existen usuarios con la información proporcionada", Toast.LENGTH_SHORT).show()
            }
        }
        if(usersList.size > 0 ){
            adapterUsers = AdapterUsers(requireActivity(), usersList)
            adapterUsers.notifyDataSetChanged()
            recyclerView.adapter = adapterUsers
        }
        else{
            Toast.makeText(context, "No existen usuarios con la información proporcionada", Toast.LENGTH_SHORT).show()
        }



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.logout).isVisible = false
        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!query.trim().isEmpty()) {
                    searchUsers(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (!newText.trim().isEmpty()) {
                    searchUsers(newText)
                }
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }
}
