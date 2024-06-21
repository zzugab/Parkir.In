package com.sugab.parkirin.customview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.sugab.parkirin.data.parking.Floor
import com.sugab.parkirin.data.parking.Parking

class ParkingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _floors = MutableLiveData<List<Floor>>()
    val floors: LiveData<List<Floor>> get() = _floors

    private val _parkingByName = MutableLiveData<Parking?>()
    val parkingByName: LiveData<Parking?> get() = _parkingByName

    init {
        loadParkingData()
    }

    private fun loadParkingData() {
        db.collection("floors").get()
            .addOnSuccessListener { result ->
                val tempFloors = mutableListOf<Floor>()
                for (document in result) {
                    val floorId = document.id.toIntOrNull()
                    val parkingList = document.get("parkingList") as? List<Map<String, Any>>

                    if (floorId != null && parkingList != null) {
                        val floor = Floor(floorId, parkingList.map { map ->
                            Parking(
                                id = (map["id"] as? Long)?.toInt() ?: 0,
                                name = map["name"] as? String ?: "",
                                isPlaced = map["isPlaced"] as? Boolean ?: false,
                                plat = map["plat"] as? String,
                                startTime = map["startTime"] as? Timestamp,
                                total = (map["total"] as? Long)?.toInt() ?: 0,
                                endTime = map["endTime"] as? Timestamp,
                                namePlaced = map["namePlaced"] as? String ?: ""
                            )
                        })
                        tempFloors.add(floor)
                    }
                }
                _floors.value = tempFloors
            }
            .addOnFailureListener { exception ->
                Log.e("ParkingViewModel", "Error fetching data: ", exception)
            }
    }

    fun getParkingById(floorId: Int, parkingId: Int): Parking? {
        val floor = _floors.value?.find { it.id == floorId }
        return floor?.parkingList?.find { it.id == parkingId }
    }

    fun getParkingByName(name: String) {
        db.collection("floors")
            .get()
            .addOnSuccessListener { result ->
                var foundParking: Parking? = null
                for (document in result) {
                    val parkingList = document.get("parkingList") as? List<Map<String, Any>>
                    parkingList?.forEach { map ->
                        val parking = Parking(
                            id = (map["id"] as? Long)?.toInt() ?: 0,
                            name = map["name"] as? String ?: "",
                            isPlaced = map["isPlaced"] as? Boolean ?: false,
                            plat = map["plat"] as? String,
                            startTime = map["startTime"] as? Timestamp,
                            total = (map["total"] as? Long)?.toInt() ?: 0,
                            endTime = map["endTime"] as? Timestamp,
                            namePlaced = map["namePlaced"] as? String ?: ""
                        )
                        if (parking.namePlaced == name) {
                            foundParking = parking
                            return@forEach
                        }
                    }
                    if (foundParking != null) break
                }
                _parkingByName.value = foundParking
            }
            .addOnFailureListener { exception ->
                Log.e("ParkingViewModel", "Error fetching data: ", exception)
                _parkingByName.value = null
            }
    }

    fun updateParking(floorId: Int, parking: Parking) {
        val floorsList = _floors.value?.toMutableList()
        val floor = floorsList?.find { it.id == floorId }
        floor?.parkingList?.let { parkingList ->
            val index = parkingList.indexOfFirst { it.id == parking.id }
            if (index != -1) {
                val mutableParkingList = parkingList.toMutableList()
                mutableParkingList[index] = parking
                val updatedFloor = floor.copy(parkingList = mutableParkingList)
                val updatedFloorsList = floorsList.toMutableList()
                val floorIndex = updatedFloorsList.indexOfFirst { it.id == floorId }
                if (floorIndex != -1) {
                    updatedFloorsList[floorIndex] = updatedFloor
                    _floors.value = updatedFloorsList

                    // Update Firestore
                    saveParkingToFirestore(floorId, mutableParkingList)
                }
            }
        }
    }

    private fun saveParkingToFirestore(floorId: Int, parkingList: List<Parking>) {
        val floorData = mapOf(
            "parkingList" to parkingList.map { parking ->
                mapOf(
                    "id" to parking.id,
                    "name" to parking.name,
                    "isPlaced" to parking.isPlaced,
                    "plat" to parking.plat,
                    "startTime" to parking.startTime,
                    "total" to parking.total,
                    "endTime" to parking.endTime,
                    "namePlaced" to parking.namePlaced

                )
            }
        )

        db.collection("floors").document(floorId.toString())
            .set(floorData)
            .addOnSuccessListener {
                Log.d("ParkingViewModel", "Parking data successfully updated")
            }
            .addOnFailureListener { e ->
                Log.e("ParkingViewModel", "Error updating parking data", e)
            }
    }

    private val _parkingByPlate = MutableLiveData<Parking?>()
    val parkingByPlate: LiveData<Parking?> = _parkingByPlate

    fun getParkingByPlate(plateNumber: String) {
        db.collection("floors")
            .get()
            .addOnSuccessListener { result ->
                var foundParking: Parking? = null

                for (document in result) {
                    val parkingList = document.get("parkingList") as? List<Map<String, Any>>
                    parkingList?.forEach { map ->
                        val parking = Parking(
                            id = (map["id"] as? Long)?.toInt() ?: 0,
                            name = map["name"] as? String ?: "",
                            isPlaced = map["isPlaced"] as? Boolean ?: false,
                            plat = map["plat"] as? String,
                            startTime = map["startTime"] as? Timestamp,
                            total = (map["total"] as? Long)?.toInt() ?: 0,
                            endTime = map["endTime"] as? Timestamp,
                            namePlaced = map["namePlaced"] as? String ?: ""
                        )

                        // Check if the parking's plate number matches the recognized plate number
                        if (parking.plat == plateNumber) {
                            foundParking = parking
                            return@forEach
                        }
                    }

                    if (foundParking != null) {
                        break
                    }
                }

                _parkingByPlate.value = foundParking
            }
            .addOnFailureListener { exception ->
                Log.e("ParkingViewModel", "Error fetching data: ", exception)
                _parkingByPlate.value = null
            }
    }
}