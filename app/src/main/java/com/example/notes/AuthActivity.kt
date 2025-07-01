package com.example.notes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.notes.databinding.ActivityAuthBinding
import com.example.notes.databinding.FragmentAddNoteBinding
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Navigation Controller
        var navHostFragment = supportFragmentManager.findFragmentById(R.id.AuthFragment)
                as NavHostFragment
        var navController = navHostFragment.navController


        //Refreshing Token


        val tokenPref = getSharedPreferences("FCMToken", MODE_PRIVATE)
        val oldToken = tokenPref.getString("Token", null)
        if (oldToken != null) {
            Log.d("FCM_MAIN", "Old Token: $oldToken")
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
                // Token deleted, now generate a new one
                FirebaseMessaging.getInstance().token
                    .addOnSuccessListener { newToken ->
                        Log.d("FCM_MAIN", "New Token Created: $newToken")
                        // Optionally save the new token again if you still want to
                        tokenPref.edit().putString("Token", newToken).apply()
                    }
                    .addOnFailureListener {
                        Log.d("FCM_MAIN", "Failed to Create Token")
                    }
            }
        } else {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { newToken ->
                    Log.d("FCM_MAIN", "Token Created: $newToken")
                    tokenPref.edit().putString("Token", newToken).apply()
                }
                .addOnFailureListener {
                    Log.d("FCM_MAIN", "Failed to Create Token")
                }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()

    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseMessaging.getInstance().deleteToken()
    }
}

class AddNote : Fragment() {
    private lateinit var binding: FragmentAddNoteBinding
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddNoteBinding.inflate(inflater,container,false)

        sharedPreferences = requireActivity().getSharedPreferences("Note_Data",
            Context.MODE_PRIVATE
        )

        val gson = Gson()
        binding.save.setOnClickListener {
            val heading = binding.heading.text.toString()
            val description = binding.description.text.toString()
            val newNote: NoteData = NoteData(heading, description)
            val existingJson = sharedPreferences.getString("notes",null)
            val type = object : TypeToken<MutableList<NoteData>>(){}.type
            val noteList : MutableList<NoteData> = if(existingJson != null) gson.fromJson(existingJson,type) else mutableListOf()
            noteList.add(newNote)
            val json = gson.toJson(noteList)
            sharedPreferences.edit().putString("notes",json).apply()
            findNavController().navigate(R.id.noteList)
        }


        return binding.root
    }
}