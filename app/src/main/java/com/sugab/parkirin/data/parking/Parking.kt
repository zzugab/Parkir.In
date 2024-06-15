package com.sugab.parkirin.data.parking

import com.google.firebase.Timestamp

data class Parking(
    val id: Int,
    var x: Float? = 0F,
    var y: Float? = 0F,
    var name: String? = "",
    var startTime: Timestamp?,
    var endTime: Timestamp?,
    var total: Int? = 0,
    var plat: String? = "",
    var isPlaced: Boolean = false,
    var namePlaced: String? = ""
)