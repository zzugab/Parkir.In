package com.sugab.parkirin.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.sugab.parkirin.data.parking.Floor
import com.sugab.parkirin.data.parking.Parking

class FirestoreManager(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()

    fun uploadParkingData(parking: Parking) {
        val parkingData = hashMapOf(
            "id" to parking.id,
            "x" to parking.x,
            "y" to parking.y,
            "name" to parking.name,
            "startTime" to parking.startTime,
            "total" to parking.total,
            "plat" to parking.plat,
            "isPlaced" to parking.isPlaced
        )

        db.collection("parkings")
            .document(parking.id.toString())
            .set(parkingData)
            .addOnSuccessListener {
                Toast.makeText(context, "Data berhasil diupload", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal mengupload data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun getFloors(callback: (List<Floor>) -> Unit) {
        db.collection("floors")
            .get()
            .addOnSuccessListener { documents ->
                val floors = documents.map { document ->
                    val id = document.getLong("id")?.toInt() ?: 0
                    val parkingList = document.get("parkingList") as List<Map<String, Any>>
                    val parkings = parkingList.map { parkingMap ->
                        Parking(
                            id = (parkingMap["id"] as Long).toInt(),
                            x = (parkingMap["x"] as Double).toFloat(),
                            y = (parkingMap["y"] as Double).toFloat(),
                            name = parkingMap["name"] as String,
                            startTime = parkingMap["startTime"] as Timestamp,
                            total = (parkingMap["total"] as Long).toInt(),
                            plat = parkingMap["plat"] as String,
                            isPlaced = parkingMap["isPlaced"] as Boolean,
                            endTime =  parkingMap["endTime"] as Timestamp
                        )
                    }
                    Floor(id, parkings)
                }
                callback(floors)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
