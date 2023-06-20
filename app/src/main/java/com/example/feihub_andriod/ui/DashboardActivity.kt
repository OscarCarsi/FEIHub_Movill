package com.example.feihub_andriod.ui

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.SingletonUser
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var myuid: String
    private lateinit var actionBar: ActionBar
    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        actionBar = supportActionBar!!
        actionBar.title = "Profile Activity"

        navigationView = findViewById(R.id.navigation)
        navigationView.setOnNavigationItemSelectedListener(selectedListener)
        actionBar.title = "Inicio"

        val fragment = HomeFragment()
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content, fragment, "")
        fragmentTransaction.commit()
    }

    private val selectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.nav_home -> {
                actionBar.title = "FeiHub"
                val fragmentHome = HomeFragment()
                val fragmentTransactionHome: FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransactionHome.replace(R.id.content, fragmentHome, "")
                fragmentTransactionHome.commit()
                true
            }
            R.id.nav_profile -> {
                actionBar.title = "Perfil"
                val username = SingletonUser.username
                val fragmentProfile = ProfileFragment.newInstance(username!!)
                val fragmentTransactionProfile: FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransactionProfile.replace(R.id.content, fragmentProfile)
                fragmentTransactionProfile.commit()
                true
            }
            R.id.nav_users -> {
                actionBar.title = "Buscar usuarios"
                val fragmentUsers = UsersFragment()
                val fragmentTransactionUsers: FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransactionUsers.replace(R.id.content, fragmentUsers, "")
                fragmentTransactionUsers.commit()
                true
            }
            R.id.nav_chat -> {
                actionBar.title = "Chats"
                val listFragmentChat = ChatListFragment()
                val fragmentTransactionChat: FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransactionChat.replace(R.id.content, listFragmentChat, "")
                fragmentTransactionChat.commit()
                true
            }
            R.id.nav_addblogs -> {
                actionBar.title = "Agregar un post"
                val fragmentAddPost = AddPostFragment()
                val fragmentTransactionAddPost: FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransactionAddPost.replace(R.id.content, fragmentAddPost, "")
                fragmentTransactionAddPost.commit()
                true
            }
            else -> false
        }
    }
}