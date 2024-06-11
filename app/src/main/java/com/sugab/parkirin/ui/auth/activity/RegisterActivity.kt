package com.sugab.parkirin.ui.auth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

            // Menampilkan ProgressBar saat proses registrasi dimulai
            showProgressBar(true)

            viewModel.register(email, password, displayName)
        }

        binding.tvAlreadyHave.setOnClickListener {
            navigateToLogin()
        }

        viewModel.user.observe(this) { user ->
            if (user != null) {
                Toast.makeText(this, "Registered as: ${user.email}", Toast.LENGTH_SHORT).show()
                // Sembunyikan ProgressBar setelah registrasi berhasil
                showProgressBar(false)
            }
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                // Sembunyikan ProgressBar jika terjadi kesalahan pada registrasi
                showProgressBar(false)
            }
        }
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Menutup LoginActivity agar tidak kembali ke login saat tombol back ditekan
    }
}
