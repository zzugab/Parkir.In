package com.sugab.parkirin.ui.valet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sugab.parkirin.data.valet.Valet

class ValetViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private val _valet = MutableLiveData<Valet?>()
    val valet: LiveData<Valet?> get() = _valet

    private val _valets = MutableLiveData<List<Valet?>>()
    val valets: LiveData<List<Valet?>> get() = _valets

    private val _employeeName = MutableLiveData<String?>()
    val employeeName: LiveData<String?> get() = _employeeName

    init {
        currentUser?.uid?.let { fetchValetsByUserId(it) }
    }

    fun fetchValets() {
        db.collection("valet")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val valets = mutableListOf<Valet?>()
                for (document in querySnapshot.documents) {
                    val valetId = document.getString("id")
                    val parkingId = document.getLong("parkingId")?.toInt() ?: 0
                    val userId = document.getString("userId") ?: ""
                    val employeeId = document.getLong("employeeId")?.toInt() ?: 0
                    val plat = document.getString("plat") ?: ""
                    val isParked = document.getBoolean("isParked") ?: false

                    fetchEmployeeName(employeeId)

                    val valet = Valet(valetId!!, parkingId, userId, employeeId, plat, isParked)
                    valets.add(valet)
                }
                _valets.value = valets
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching valets: ", exception)
            }
    }

    private fun fetchValetsByUserId(userId: String) {
        db.collection("valet")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val valetId = document.getString("id")
                    val parkingId = document.getLong("parkingId")?.toInt() ?: 0
                    val employeeId = document.getLong("employeeId")?.toInt() ?: 0
                    val plat = document.getString("plat") ?: ""
                    val isParked = document.getBoolean("isParked") ?: false

                    fetchEmployeeName(employeeId)

                    val valet = Valet(valetId!!, parkingId, currentUser!!.uid, employeeId, plat, isParked)
                    _valet.value = valet
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching valets: ", exception)
            }
    }

    private fun fetchEmployeeName(employeeId: Int) {
        db.collection("valetEmployees")
            .whereEqualTo("id", employeeId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()
                    val employeeName = document.getString("name")
                    _employeeName.value = employeeName
                } else {
                    Log.e("Firestore", "Employee document not found for id: $employeeId")
                    _employeeName.value = "Unknown" // Set a default value or handle the case accordingly
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching employee name for id: $employeeId", exception)
                _employeeName.value = "Unknown" // Set a default value or handle the failure accordingly
            }
    }

    fun updateEmployeeStatus(employeeId: Int) {
        db.collection("valetEmployees")
            .whereEqualTo("id", employeeId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()
                    val employeeName = document.getString("name")
                    val isReady = true

                    // Update isReady menjadi true di Firestore
                    db.collection("valetEmployees")
                        .document(document.id)
                        .update("isReady", isReady)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Employee status updated to isReady = true")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firestore", "Error updating employee status: $exception")
                        }
                } else {
                    Log.e("Firestore", "Employee document not found for id: $employeeId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching employee document: $exception")
            }
    }


}
