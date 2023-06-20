package com.example.feihub_andriod.ui
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.User

import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterUsers(private val context: Context, private val list: MutableList<User>) : RecyclerView.Adapter<AdapterUsers.MyHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_users, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val username = list[position].username
        val profilePhoto = list[position].profilePhoto

        holder.name.text = username
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                if(profilePhoto != null){
                    val requestOptions = RequestOptions()
                        .placeholder(R.drawable.usuario)
                        .error(R.drawable.ic_errorimage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                    withContext(Dispatchers.Main) {
                        Glide.with(context)
                            .load(profilePhoto)
                            .apply(requestOptions)
                            .into(holder.profiletv)
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profiletv: CircleImageView = itemView.findViewById(R.id.imageUsers)
        val name: TextView = itemView.findViewById(R.id.usernameUsers)
    }
}
