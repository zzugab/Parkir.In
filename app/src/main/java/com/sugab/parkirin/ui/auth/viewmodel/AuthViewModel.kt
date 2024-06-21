package com.sugab.parkirin.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    //Live data user
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    //Handle error response
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    //Live data role
    private val _role = MutableLiveData<String?>()
    val role: LiveData<String?> get() = _role

    fun register(email: String, password: String, displayName: String, role: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()

                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    saveUserToFirestore(it.uid, email, displayName, role)
                                } else {
                                    _error.value = updateTask.exception?.message
                                }
                            }
                    }
                } else {
                    _error.value = task.exception?.message
                }
            }
    }

    private fun saveUserToFirestore(uid: String, email: String, displayName: String, role: String) {
        val userMap = hashMapOf(
            "uid" to uid,
            "email" to email,
            "displayName" to displayName,
            "role" to role
        )
        db.collection("users").document(uid).set(userMap)
            .addOnSuccessListener {
                _user.value = firebaseAuth.currentUser
            }
            .addOnFailureListener { e ->
                _error.value = e.message
            }
    }

    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    _user.value = user
                    user?.let { fetchUserRole(it.uid) }
                } else {
                    _error.value = task.exception?.message
                }
            }
    }

    private fun fetchUserRole(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    _role.value = document.getString("role")
                } else {
                    _error.value = "Role not found"
                }
            }
            .addOnFailureListener { exception ->
                _error.value = exception.message
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        _user.value = null
        _role.value = null
    }
}
