package com.example.notes

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.NoteAdapter
import com.example.notes.databinding.ActivityMainBinding
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var noteList: MutableList<NoteData> = mutableListOf()
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var navController: NavController
    private lateinit var firebaseMessage: FirebaseMessaging


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//?        NavController
        var navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        var currentFragment =
            navHostFragment.childFragmentManager.fragments.firstOrNull { it.isVisible }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.noteList -> binding.fab.visibility = View.VISIBLE
                else -> binding.fab.visibility = View.GONE
            }
        }

        //FAB action --> fragment navigation
        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_noteList_to_addNote)
        }

    }


    //    navigation helper
    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    // Reading Data
    @SuppressLint("NotifyDataSetChanged")
    private fun readData() {
        val json = sharedPreferences.getString("notes", null)
        val type = object : TypeToken<MutableList<NoteData>>() {}.type
        val updateList: MutableList<NoteData> =
            if (!json.isNullOrEmpty()) Gson().fromJson(json, type) else mutableListOf()
        noteList.clear()
        noteList.addAll(updateList)
        noteAdapter.notifyDataSetChanged()
    }

    //    Saving Data
    private fun saveNotes() {
        val json = Gson().toJson(noteList)
        sharedPreferences.edit().putString("notes", json).apply()
    }
}

const val channelId = "notification_channel"
const val channelName = "com.example.notes"


class FBMS: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val tokenPreferences = getSharedPreferences("FCMToken",MODE_PRIVATE)
        tokenPreferences.edit().putString("Token",token)
    }


    override fun onMessageReceived(message: RemoteMessage) {
        if(message.getNotification() != null){
            generateNotification(message.notification!!.title!!,message.notification!!.body!!)
        }
    }

    fun getRemoteView(title:String,message:String) : RemoteViews {
        val remoteViews = RemoteViews("com.example.notes", R.layout.notification)
        remoteViews.setTextViewText(R.id.title,title)
        remoteViews.setTextViewText(R.id.message,message)
        remoteViews.setImageViewResource(R.id.logo, R.drawable.save_instagram)
        return remoteViews
    }

    fun  generateNotification(title:String,message:String){
        val intent = Intent(this, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT
                 or PendingIntent.FLAG_IMMUTABLE)

        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,
        channelId).setSmallIcon(R.drawable.save_instagram)
            .setAutoCancel(true).setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true).setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title,message))

        val notficationManager = getSystemService(NOTIFICATION_SERVICE) as
                NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notficationManager.createNotificationChannel(notificationChannel)
        }
        notficationManager.notify(0,builder.build())
    }

}

class NoteAdapter(private val noteList: List<NoteData>, private val onDelete: (Int) -> Unit, private val onNoteClick: (NoteData) -> Unit) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val heading = itemView.findViewById<TextView>(R.id.itemHeading)
        val delete = itemView.findViewById<ImageView>(R.id.delete)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = noteList[position]
        holder.heading.text = note.Heading
//        holder.description.text = note.Description

        holder.delete.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onDelete(pos)
            }
        }
        holder.itemView.setOnClickListener {
            onNoteClick(note)
        }
    }

}