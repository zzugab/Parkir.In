package com.sugab.parkirin.ui.auth.activity

import android.content.Intent
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
            val role = "customer" // Set default role for registration

            viewModel.register(email, password, displayName, role)
        }

        binding.tvAlreadyHave.setOnClickListener {
            navigateToLogin()
        }

        viewModel.error.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.user.observe(this, Observer { user ->
            user?.let {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                showProgressBar(false)
                navigateToLogin()
            }
        })
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
