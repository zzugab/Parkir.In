package com.sugab.parkirin.utils

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.sugab.parkirin.data.parking.Floor
import com.sugab.parkirin.data.parking.Parking

class FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()

    fun saveFloor(floor: Floor, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("floors")
            .document(floor.id.toString())
            .set(floor)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getFloor(floorId: Int, onSuccess: (Floor?) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("floors")
            .document(floorId.toString())
            .get()
            .addOnSuccessListener { document ->
                val floor = document.toObject<Floor>()
                onSuccess(floor)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateParking(floorId: Int, parking: Parking, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("floors")
            .document(floorId.toString())
            .update("parkingList", FieldValue.arrayUnion(parking))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun listenForFloorUpdates(floorId: Int, callback: (Floor?) -> Unit) {
        db.collection("floors")
            .document(floorId.toString())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    callback(null)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val floor = snapshot.toObject<Floor>()
                    callback(floor)
                } else {
                    callback(null)
                }
            }
    }
}
