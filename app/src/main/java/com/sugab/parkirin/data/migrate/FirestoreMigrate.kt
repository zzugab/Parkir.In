package com.sugab.parkirin.data.migrate

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.sugab.parkirin.data.parking.Floor
import com.sugab.parkirin.data.parking.Parking
import com.sugab.parkirin.data.valet.ValetEmployee

//Kelas buat migrate data ke firestore, aku pakainya cuman sekali, habis itu ngga
class FirestoreMigrate(private val db: FirebaseFirestore) {

    fun migrateDataFloorAndParking() {
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

    // Migrate emoplyee valet
    fun migrateValetEmployee() {
        // Data untuk setiap karyawan valet
        val valetEmployees = createValetEmployee()

        // Iterasi melalui setiap karyawan valet dan mengunggahnya ke Firestore
        valetEmployees.forEach { employee ->
            // Koleksi 'valetEmployees', dokumen dengan id berdasarkan employee.id
            val valetEmployeeDocRef = db.collection("valetEmployees").document(employee.id.toString())

            // Data yang akan di-upload ke Firestore
            val employeeData = hashMapOf(
                "id" to employee.id,
                "name" to employee.name,
                "isReady" to employee.isReady
            )

            // Upload data ke Firestore
            valetEmployeeDocRef.set(employeeData)
                .addOnSuccessListener {
                    println("Data karyawan valet ${employee.name} berhasil diupload")
                }
                .addOnFailureListener { e ->
                    println("Gagal mengupload data karyawan valet ${employee.name}: $e")
                }
        }
    }

    // Data Valet Employee
    private fun createValetEmployee(): List<ValetEmployee> {
        return listOf(
            ValetEmployee(id = 1, name = "Pudge", isReady = true),
            ValetEmployee(id = 2, name = "Juggernaut", isReady = true),
            ValetEmployee(id = 3, name = "Invoker", isReady = true),
            ValetEmployee(id = 4, name = "Omni", isReady = true),
            ValetEmployee(id = 5, name = "Oman", isReady = true)
        )
    }
}
