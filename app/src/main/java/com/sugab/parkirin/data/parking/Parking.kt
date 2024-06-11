package com.sugab.parkirin.data.parking

data class Parking(
    val id: Int,
    var x: Float? = 0F,
    var y: Float? = 0F,
    var name: String? = "",
    var startTime: Long? = null,
    var total: Int? = 0,
    var plat: String? = "",
    var isPlaced: Boolean = false
)