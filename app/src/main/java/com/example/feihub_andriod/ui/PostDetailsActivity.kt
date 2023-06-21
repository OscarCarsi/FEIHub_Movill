package com.example.feihub_andriod.ui

import android.app.Activity
import androidx.appcompat.app.ActionBar
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.feihub_andriod.R
import com.example.feihub_andriod.data.model.Comment
import com.example.feihub_andriod.data.model.Posts
import com.example.feihub_andriod.data.model.SingletonUser
import com.example.feihub_andriod.data.model.User
import com.example.feihub_andriod.services.PostsAPIServices
import com.example.feihub_andriod.services.UsersAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var hisuid: String
    private lateinit var ptime: Date
    private lateinit var myuid: String
    private lateinit var myname: String
    private lateinit var myemail: String
    private lateinit var mydp: String
    private var uimage: String? = null
    private lateinit var post: Posts
    private var plike: Int = 0
    private var dislikes: Int = 0
    private lateinit var hisdp: String
    private lateinit var hisname: String
    private lateinit var picture: ImageView
    private lateinit var image: ImageView
    private lateinit var name: TextView
    private lateinit var time: TextView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var like: TextView
    private lateinit var dislike: TextView
    private lateinit var tcomment: TextView
    private lateinit var more: ImageButton
    private lateinit var likebtn: Button
    private lateinit var dislikebtn: Button
    private lateinit var reportbtn: Button
    private lateinit var profile: LinearLayout
    private lateinit var comment: EditText
    private lateinit var sendb: ImageButton
    private lateinit var recyclerView: RecyclerView
    private var commentList: MutableList<Comment>? = null
    private lateinit var adapterComment: AdapterComment
    private lateinit var imagep: ImageView
    private var mlike = false
    private lateinit var actionBar: ActionBar
    private lateinit var progressDialog: ProgressDialog
    private val usersAPIServices = UsersAPIServices()
    private val postsAPIServices = PostsAPIServices()
    val buttonSize = 80
    private var likeStatus = false
    private var dislikeStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        actionBar = supportActionBar!!
        actionBar.title = "Post Details"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        post = (intent.getSerializableExtra("post") as? Posts)!!

        recyclerView = findViewById(R.id.recyclecomment)
        picture = findViewById(R.id.pictureco)
        image = findViewById(R.id.pimagetvco)
        name = findViewById(R.id.unameco)
        time = findViewById(R.id.utimeco)
        more = findViewById(R.id.morebtn)
        title = findViewById(R.id.ptitleco)
        description = findViewById(R.id.descriptco)
        tcomment = findViewById(R.id.pcommenttv)
        like = findViewById(R.id.plikebco)
        dislike = findViewById(R.id.dislikesText)
        likebtn = findViewById(R.id.like)
        comment = findViewById(R.id.typecommet)
        sendb = findViewById(R.id.sendcomment)
        dislikebtn = findViewById(R.id.dislike)
        profile = findViewById(R.id.profilelayoutco)
        reportbtn = findViewById(R.id.report)
        progressDialog = ProgressDialog(this)


        val likeDrawable = ContextCompat.getDrawable(this, R.drawable.ic_like)
        likeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
        val dislikeDrawable = ContextCompat.getDrawable(this, R.drawable.ic_dislike)
        dislikeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
        val reportDrawable = ContextCompat.getDrawable(this, R.drawable.ic_report)
        reportDrawable?.setBounds(0, 0, buttonSize, buttonSize)

        likebtn.setCompoundDrawables(likeDrawable, null, null, null)
        dislikebtn.setCompoundDrawables(dislikeDrawable, null, null, null)
        reportbtn.setCompoundDrawables(reportDrawable, null, null, null)
        loadPostInfo()


        loadComments()

        sendb.setOnClickListener {
            postComment()
        }

        likebtn.setOnClickListener {
            likePost()
        }
        dislikebtn.setOnClickListener {
            dislikedPost()
        }
        reportbtn.setOnClickListener {
            report()
        }

    }
    private fun report(){
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val statusCode = postsAPIServices.addReport(post.id!!)
                if(statusCode == 200){
                    this@PostDetailsActivity.runOnUiThread {
                        Toast.makeText(this@PostDetailsActivity, "Publicación reportada", Toast.LENGTH_LONG).show()
                    }

                }else{
                    this@PostDetailsActivity.runOnUiThread {
                        Toast.makeText(this@PostDetailsActivity, "No pudimos reportar esta publicacíon", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun loadComments() {
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        commentList = post.comments
        if(commentList != null){
            adapterComment = AdapterComment(applicationContext, commentList!!)
            recyclerView.adapter = adapterComment
        }

    }


    private fun likePost() {
        if(likeStatus == false){
            plike++
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val statusCode = postsAPIServices.addLike(post.id!!)
                    if (statusCode == 200) {
                        this@PostDetailsActivity.runOnUiThread {
                            val likeDrawable = ContextCompat.getDrawable(this@PostDetailsActivity, R.drawable.ic_liked)
                            likeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
                            likebtn.setCompoundDrawables(likeDrawable, null, null, null)
                            like.text = "$plike Likes"
                            likeStatus = true
                        }
                    } else {
                        this@PostDetailsActivity.runOnUiThread {
                            Toast.makeText(this@PostDetailsActivity, "No pudimos agregar el like", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }else{
            plike--
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val statusCode = postsAPIServices.removelike(post.id!!)
                    if(statusCode == 200){
                        this@PostDetailsActivity.runOnUiThread {
                            val likeDrawable = ContextCompat.getDrawable(this@PostDetailsActivity, R.drawable.ic_like)
                            likeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
                            likebtn.setCompoundDrawables(likeDrawable, null, null, null)
                            like.text = "$plike Likes"
                            likeStatus = false
                        }
                    }else{
                        this@PostDetailsActivity.runOnUiThread {
                            Toast.makeText(this@PostDetailsActivity, "No pudimos remover el like", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
    private fun dislikedPost() {
        if(dislikeStatus == false){
            dislikes++
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val statusCode = postsAPIServices.addDislike(post.id!!)
                    if (statusCode == 200) {
                        this@PostDetailsActivity.runOnUiThread {
                            val likeDrawable = ContextCompat.getDrawable(this@PostDetailsActivity, R.drawable.ic_disliked)
                            likeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
                            dislikebtn.setCompoundDrawables(likeDrawable, null, null, null)
                            dislike.text = "$dislikes Dislikes"
                            dislikeStatus = true
                        }
                    } else {
                        this@PostDetailsActivity.runOnUiThread {
                            Toast.makeText(this@PostDetailsActivity, "No pudimos agregar el dislike", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }else{
            dislikes--
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val statusCode = postsAPIServices.removeDislike(post.id!!)
                    if(statusCode == 200){
                        this@PostDetailsActivity.runOnUiThread {
                            val likeDrawable = ContextCompat.getDrawable(this@PostDetailsActivity, R.drawable.ic_dislike)
                            likeDrawable?.setBounds(0, 0, buttonSize, buttonSize)
                            dislikebtn.setCompoundDrawables(likeDrawable, null, null, null)
                            dislike.text = "$dislikes Dislikes"
                            dislikeStatus = false
                        }
                    }else{
                        this@PostDetailsActivity.runOnUiThread {
                            Toast.makeText(this@PostDetailsActivity, "No pudimos remover el dislike", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
    private fun postComment() {
        val newComment = Comment()
        newComment.author = SingletonUser.username!!
        newComment.dateOfComment = getCurrentDate()
        newComment.body = comment.text.toString()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val statusCode = postsAPIServices.addComment(newComment, post.id!!)
                this@PostDetailsActivity.runOnUiThread() {
                    if (statusCode == 200) {
                        Toast.makeText(this@PostDetailsActivity, "Comentario agregado", Toast.LENGTH_LONG).show()
                        comment.setText("")
                        commentList!!.add(newComment)
                        loadComments()
                    } else {
                        Toast.makeText(this@PostDetailsActivity, "No pudimos agregar tu comentario, inténtalo más tarde", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    fun getCurrentDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1) // Restar un día
        return calendar.time
    }

    private var count = false





    private fun loadPostInfo() {
        hisname = post.author!!
        val ptitle: String = post.title!!
        val descriptions: String = post.body!!
        ptime = post.dateOfPublish!!
        plike = post.likes!!
        dislikes = post.dislikes!!
        val timedate = post.dateOfPublish
        var dislikes: Int = post.dislikes!!
        if(post.photos!!.size > 0){
            uimage = post.photos?.get(0)?.url!!
        }
        name.text = hisname
        title.text = ptitle
        description.text = descriptions
        like.text = "$plike Likes"
        dislike.text = "Dislikes $dislikes"
        time.text = timedate.toString()
        if (uimage == null) {
            image.visibility = View.GONE
        } else {
            image.visibility = View.VISIBLE
            val requestOptions = RequestOptions()
                .error(R.drawable.ic_errorimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
            Glide.with(this)
                .load(uimage)
                .apply(requestOptions)
                .into(image)
        }
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val userObtained = usersAPIServices.getUser(hisname!!)
                if(userObtained!!.profilePhoto != null){
                    val requestOptions = RequestOptions()
                        .placeholder(R.drawable.usuario)
                        .error(R.drawable.ic_errorimage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                    withContext(Dispatchers.Main) {
                        Glide.with(this@PostDetailsActivity)
                            .load(userObtained.profilePhoto)
                            .apply(requestOptions)
                            .into(picture)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
