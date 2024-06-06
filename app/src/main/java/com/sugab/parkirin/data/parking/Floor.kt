package com.sugab.parkirin.data.parking

data class Floor(
    val id: Int,
    val parkingList: List<Parking>
) {
    val left: Int
        get() = parkingList.count { !it.isPlaced }
}
