package com.sugab.parkirin.ui.auth.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.sugab.parkirin.databinding.ActivityRegisterBinding
import com.sugab.parkirin.ui.auth.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val displayName = binding.etUsername.text.toString()
            viewModel.register(email, password, displayName)
        }

        viewModel.user.observe(this, Observer { user ->
            if (user != null) {
                Toast.makeText(this, "Registered as: ${user.email}", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.error.observe(this, Observer { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        })
    }
}