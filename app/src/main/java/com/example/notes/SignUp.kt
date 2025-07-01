package com.example.notes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.notes.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignUp : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSignUpBinding.inflate(inflater,container,false)
        handleBackPress()


        binding.SignIn.setOnClickListener{
            val email = binding.UserName.text.toString()
            val password = binding.password.text.toString()
            val cnf_password = binding.cnfPassword.text.toString()

            if(email.isEmpty() || password.isEmpty() || cnf_password.isEmpty()){
                Toast.makeText(context,"Please fill all the fields",Toast.LENGTH_SHORT).show()
            }
            else if(password != cnf_password){
                Toast.makeText(context,"Password does not match",Toast.LENGTH_SHORT).show()
            }
            else{

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                    task ->
                    if(task.isSuccessful){
                        Toast.makeText(context,"Account created successfully",Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_signUp_to_login)
                    }
                    else{
                        Toast.makeText(context,"Account creation failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }

    private fun handleBackPress(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                   findNavController().navigate(R.id.action_signUp_to_login)
                }
            })
    }


}