package com.example.notes

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.notes.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class Login : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth= FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)

        binding.Login.setOnClickListener {
            val email = binding.UserName.text.toString()
            val password = binding.pwd.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(context,"Please fill all the fields",Toast.LENGTH_SHORT).show()
            }else{
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    task->
                    if(task.isSuccessful){
                        val intent = Intent(context,MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(context, "Login Credentials Wrong", Toast.LENGTH_SHORT)
                            .show()
                        binding.UserName.text?.clear()
                        binding.pwd.text?.clear()
                    }
                }
            }


        }

        binding.SignUp.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signUp)
        }


        binding.FP.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgotPass)
        }

        return binding.root
    }
}