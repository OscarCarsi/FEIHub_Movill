package com.example.feihub_andriod.ui
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.Comment
import com.example.feihub_andriod.services.UsersAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private val usersAPIServices: UsersAPIServices = UsersAPIServices()
class AdapterComment(
    private val context: Context,
    private val list: MutableList<Comment>,
) : RecyclerView.Adapter<AdapterComment.MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val commentId = list[position].commentId
        val username = list[position].author
        val body = list[position].body
        val timestamp = list[position].commentId
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        val timedate = DateFormat.format("dd/MM/yyyy", calendar).toString()

        holder.name.text = username
        holder.time.text = timedate
        holder.comment.text = body
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val userObtained = usersAPIServices.getUser(username!!)
                if (userObtained != null){
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
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profilePhoto: ImageView = itemView.findViewById(R.id.loadcomment)
        var name: TextView = itemView.findViewById(R.id.commentname)
        var comment: TextView = itemView.findViewById(R.id.commenttext)
        var time: TextView = itemView.findViewById(R.id.commenttime)
    }
}