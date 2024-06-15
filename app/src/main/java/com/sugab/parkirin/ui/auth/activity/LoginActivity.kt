package com.sugab.parkirin.ui.auth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sugab.parkirin.databinding.ActivityLoginBinding
import com.sugab.parkirin.ui.auth.viewmodel.AuthViewModel
import com.sugab.parkirin.ui.home.customer.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Menampilkan ProgressBar saat proses login dimulai
            showProgressBar(true)

            viewModel.login(email, password)
        }

        viewModel.user.observe(this) { user ->
            if (user != null) {
                Toast.makeText(this, "Logged In: ${user.displayName}", Toast.LENGTH_SHORT).show()
                navigateActivity("main")
            }
        }

        binding.tvDontHave.setOnClickListener {
            navigateActivity("reg")
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                // Sembunyikan ProgressBar jika terjadi kesalahan pada login
                showProgressBar(false)
            }
        }
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility =
            if (show) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun navigateActivity(opsi: String) {
        if (opsi == "main") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Menutup LoginActivity agar tidak kembali ke login saat tombol back ditekan
        } else if (opsi == "reg") {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish() // Menutup LoginActivity agar tidak kembali ke login saat tombol back ditekan
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.user.removeObservers(this)
        viewModel.error.removeObservers(this)
    }
}
