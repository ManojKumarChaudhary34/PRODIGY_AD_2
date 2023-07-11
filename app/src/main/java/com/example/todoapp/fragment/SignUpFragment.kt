package com.example.todoapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun registerEvents() {

        binding.authTextView.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }
      binding.btnNext.setOnClickListener {
          val email= binding.emailEt.text.toString().trim()
          val pass= binding.passEt.text.toString().trim()
          val rePass= binding.rePassEt.text.toString().trim()

          if (email.isNotEmpty() && pass.isNotEmpty() && rePass.isNotEmpty()){
              if (pass == rePass){
                  binding.progressBar.visibility= View.VISIBLE
                  auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                      if (it.isSuccessful){
                          Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                          navController.navigate(R.id.action_signUpFragment_to_homeFragment)
                      }else{
                          Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                      }
                      binding.progressBar.visibility= View.GONE
                  }
              }else{
                  Toast.makeText(context, "Password doesn't match", Toast.LENGTH_SHORT).show()
              }
          }else{
              Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
          }
      }
    }

    private fun init(view: View) {
        navController= Navigation.findNavController(view)
        auth= FirebaseAuth.getInstance()
    }

}