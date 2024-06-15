package com.sugab.parkirin.data.migrate

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.sugab.parkirin.data.parking.Floor
import com.sugab.parkirin.data.parking.Parking

//Kelas buat migrate data ke firestore, aku pakainya cuman sekali, habis itu ngga
class FirestoreMigrate(private val db: FirebaseFirestore) {

    fun migrateData() {
        // Data untuk setiap lantai
        val floors = listOf(
            Floor(
                id = 0,
                parkingList = createParking(1, "A")
            ),
            Floor(
                id = 1,
                parkingList = createParking(2, "B")
            ),
            Floor(
                id = 2,
                parkingList = createParking(3, "C")
            )
        )

        // Iterasi melalui setiap lantai dan mengunggahnya ke Firestore
        floors.forEach { floor ->
            // Koleksi 'floors', dokumen dengan id berdasarkan floor.id
            val floorDocRef = db.collection("floors").document(floor.id.toString())

            // Membuat map untuk data parkir di dalam lantai ini
            val parkingList = floor.parkingList.map { parking ->
                hashMapOf(
                    "id" to parking.id,
                    "x" to parking.x,
                    "y" to parking.y,
                    "name" to parking.name,
                    "isPlaced" to parking.isPlaced,
                    "plat" to parking.plat,
                    "startTime" to parking.startTime,
                    "total" to parking.total,
                    "namePlaced" to parking.namePlaced,
                    "endTime" to parking.endTime
                )
            }

            // Data yang akan di-upload ke Firestore
            val floorData = hashMapOf(
                "id" to floor.id,
                "parkingList" to parkingList
            )

            // Upload data ke Firestore
            floorDocRef.set(floorData)
                .addOnSuccessListener {
                    println("Data lantai ${floor.id} berhasil diupload")
                }
                .addOnFailureListener { e ->
                    println("Gagal mengupload data lantai ${floor.id}: $e")
                }
        }
    }

    // Fungsi untuk membuat data parkir berdasarkan floorId dan label
    private fun createParking(floorId: Int, label: String): List<Parking> {
        return (1..20).map { index ->
            Parking(
                id = (floorId - 1) * 20 + index,
                x = 0F,
                y = 0F,
                total = 0,
                plat = "",
                name = "$label$index",
                isPlaced = false,
                startTime = Timestamp.now(),
                namePlaced = "",
                endTime = Timestamp.now()
            )
        }
    }
}
