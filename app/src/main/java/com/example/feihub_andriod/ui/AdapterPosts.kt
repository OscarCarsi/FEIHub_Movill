package com.example.feihub_andriod.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.Comment
import com.example.feihub_andriod.data.model.Posts
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.services.PostsAPIServices
import com.example.feihub_andriod.services.UsersAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AdapterPosts(private val context: Context, private val modelPosts: MutableList<Posts>) :
    RecyclerView.Adapter<AdapterPosts.MyHolder>() {
    private var mprocesslike = false
    private val usersAPIServices = UsersAPIServices()
    private val postsAPIServices = PostsAPIServices()
    private var likeStatus = false
    private var dislikeStatus = false

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): MyHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(@NonNull holder: MyHolder, position: Int) {
        val id: String = modelPosts[position].id!!
        val username: String = modelPosts[position].author!!
        val title: String = modelPosts[position].title!!
        val body: String = modelPosts[position].body!!
        val date: Date = modelPosts[position].dateOfPublish!!
        var likes: Int = modelPosts[position].likes!!
        var dislikes: Int = modelPosts[position].dislikes!!
        var image: String? = null
        if(modelPosts[position].photos!!.size > 0){
             image = modelPosts[position].photos?.get(0)?.url!!
        }
        val comments: MutableList<Comment>? = modelPosts[position].comments
        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH)
        val timedate: String = DateFormat.format("dd/MM/yyyy", calendar).toString()
        holder.username.text = username
        holder.title.text = title
        holder.body.text = body
        holder.time.text = date.toString()
        holder.like.text = "$likes Likes"
        holder.dislikes.text = "$dislikes Dislikes"
        val buttonSize = 80

        val likeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_like)
        likeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
        val dislikeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_dislike)
        dislikeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
        val commentDrawable = ContextCompat.getDrawable(context, R.drawable.ic_comment)
        commentDrawable?.setBounds(0, 0, buttonSize, buttonSize)
        val reportDrawable = ContextCompat.getDrawable(context, R.drawable.ic_report)
        reportDrawable?.setBounds(0, 0, buttonSize, buttonSize)

        holder.likebtn.setCompoundDrawables(likeDrawable, null, null, null)
        holder.dislikebtn.setCompoundDrawables(dislikeDrawable, null, null, null)
        holder.commentbtn.setCompoundDrawables(commentDrawable, null, null, null)
        holder.reportbtn.setCompoundDrawables(reportDrawable, null, null, null)

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val userObtained = usersAPIServices.getUser(username!!)
                if(userObtained!!.profilePhoto != null){
                    val requestOptions = RequestOptions()
                        .placeholder(R.drawable.usuario)
                        .error(R.drawable.ic_errorimage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                    withContext(Dispatchers.Main) {
                        Glide.with(context)
                            .load(userObtained.profilePhoto)
                            .apply(requestOptions)
                            .into(holder.profilePhoto)
                    }
                }
            }
        }
        if(image != null){
            holder.image.visibility = View.VISIBLE
            val requestOptions = RequestOptions()
                .error(R.drawable.ic_errorimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
            Glide.with(context)
                .load(image)
                .apply(requestOptions)
                .into(holder.image)
        }
        else{
            holder.image.visibility = View.INVISIBLE
        }
        holder.likebtn.setOnClickListener {
            val plike = modelPosts[position].likes
            mprocesslike = true
            val postid = modelPosts[position].id

        }
        holder.more.setOnClickListener {
            showMoreOptions(holder.more, username,id)
                    }
        holder.commentbtn.setOnClickListener {

            val intent = Intent(context, PostDetailsActivity::class.java)
            intent.putExtra("post", modelPosts[position])
            context.startActivity(intent)
        }
        holder.likebtn.setOnClickListener {
            if(likeStatus == false){
                likes++
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        val statusCode = postsAPIServices.addLike(id)
                        if (statusCode == 200) {
                            (context as Activity).runOnUiThread {
                                val likeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_liked)
                                likeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
                                holder.likebtn.setCompoundDrawables(likeDrawable, null, null, null)
                                holder.like.text = "$likes Likes"
                                likeStatus = true
                            }
                        } else {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "No pudimos agregar el like", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }else{
                likes--
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        val statusCode = postsAPIServices.removelike(id)
                        if(statusCode == 200){
                            (context as Activity).runOnUiThread {
                                val likeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_like)
                                likeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
                                holder.likebtn.setCompoundDrawables(likeDrawable, null, null, null)
                                holder.like.text = "$likes Likes"
                                likeStatus = false
                            }
                        }else{
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "No pudimos remover el like", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
        holder.dislikebtn.setOnClickListener {
            if(dislikeStatus == false){
                dislikes++
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        val statusCode = postsAPIServices.addDislike(id)
                        if(statusCode == 200){
                            (context as Activity).runOnUiThread {
                                val likeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_disliked)
                                likeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
                                holder.dislikebtn.setCompoundDrawables(likeDrawable, null, null, null)
                                dislikeStatus = true
                                holder.dislikes.text = "$dislikes Dislikes"
                            }

                        }else{
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "No pudimos agregar el dislike", Toast.LENGTH_LONG).show()
                            }

                        }
                    }
                }
            }else{
                dislikes--
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        val statusCode = postsAPIServices.removeDislike(id)
                        if(statusCode == 200){
                            (context as Activity).runOnUiThread {
                                val likeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_dislike)
                                likeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
                                holder.dislikebtn.setCompoundDrawables(likeDrawable, null, null, null)
                                dislikeStatus = false
                                holder.dislikes.text = "$dislikes Dislikes"
                            }

                        }else{
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "No puedimos remover el dislike", Toast.LENGTH_LONG).show()
                            }

                        }
                    }
                }
            }
        }
        holder.reportbtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val statusCode = postsAPIServices.addReport(id)
                    if(statusCode == 200){
                        (context as Activity).runOnUiThread {
                            Toast.makeText(context, "Publicación reportada", Toast.LENGTH_LONG).show()
                        }

                    }else{
                        (context as Activity).runOnUiThread {
                            Toast.makeText(context, "No puedimos reportar esta publicacíon", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun showMoreOptions(more: ImageButton, username: String, idPos: String) {
        val popupMenu = PopupMenu(context, more, Gravity.END)
        if (SingletonUser.username == username) {
            popupMenu.menu.add(Menu.NONE, 0, 0, "DELETE")
        }
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val statusCode = postsAPIServices.deletePost(idPos)
                    if(statusCode == 200){
                        (context as Activity).runOnUiThread {
                            Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_LONG).show()
                        }

                    }else{
                        (context as Activity).runOnUiThread {
                            Toast.makeText(context, "No pudimos eliminar la publicación inténtalo más tarde", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            false
        }
        popupMenu.show()
    }


    override fun getItemCount(): Int {
        return modelPosts.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profilePhoto: ImageView = itemView.findViewById(R.id.profilePhotoPosts)
        var image: ImageView = itemView.findViewById(R.id.postPhoto)
        var username: TextView = itemView.findViewById(R.id.usernameText)
        var time: TextView = itemView.findViewById(R.id.dateText)
        var more: ImageButton = itemView.findViewById(R.id.morebtn)
        var title: TextView = itemView.findViewById(R.id.titleText)
        var body: TextView = itemView.findViewById(R.id.bodyText)
        var like: TextView = itemView.findViewById(R.id.likesText)
        var dislikes: TextView = itemView.findViewById(R.id.dislikesText)
        var likebtn: Button = itemView.findViewById(R.id.like)
        var dislikebtn: Button = itemView.findViewById(R.id.dislike)
        var reportbtn: Button = itemView.findViewById(R.id.report)
        var commentbtn: Button = itemView.findViewById(R.id.comment)
        var profile: LinearLayout = itemView.findViewById(R.id.profilelayout)

    }
}