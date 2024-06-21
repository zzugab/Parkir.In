package com.sugab.parkirin.ui.auth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.sugab.parkirin.data.migrate.FirestoreMigrate
import com.sugab.parkirin.databinding.ActivityLoginBinding
import com.sugab.parkirin.ui.auth.viewmodel.AuthViewModel
import com.sugab.parkirin.ui.home.admin.AdminActivity
import com.sugab.parkirin.ui.home.customer.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private val db = FirebaseFirestore.getInstance()

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

        val firestoreMigrate = FirestoreMigrate(db)
        firestoreMigrate.migrateDataFloorAndParking()
        firestoreMigrate.migrateValetEmployee()

        viewModel.user.observe(this) { user ->
            if (user != null) {
                Toast.makeText(this, "Logged In: ${user.displayName}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.role.observe(this) { role ->
            showProgressBar(false)
            if (role != null) {
                navigateToActivityBasedOnRole(role)
            }
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                // Sembunyikan ProgressBar jika terjadi kesalahan pada login
                showProgressBar(false)
            }
        }

        binding.tvDontHave.setOnClickListener {
            navigateToRegisterActivity()
        }
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun navigateToActivityBasedOnRole(role: String) {
        val intent = when (role) {
            "admin" -> Intent(this, AdminActivity::class.java)
            "customer" -> Intent(this, MainActivity::class.java)
            else -> null
        }

        if (intent != null) {
            startActivity(intent)
            finish()
        }
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish() // Menutup LoginActivity agar tidak kembali ke login saat tombol back ditekan
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.user.removeObservers(this)
        viewModel.error.removeObservers(this)
        viewModel.role.removeObservers(this)
    }
}
