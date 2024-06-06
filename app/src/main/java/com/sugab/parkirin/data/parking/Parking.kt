package com.sugab.parkirin.data.parking

data class Parking(
    val id: Int,
    var x: Float? = 0F,
    var y: Float? = 0F,
    var name: String,
    var time: Float = 0f,
    var type: String,
    var isPlaced: Boolean = false
)
