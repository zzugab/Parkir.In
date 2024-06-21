package com.sugab.parkirin.data.valet

data class Valet(
    val id: String,
    var parkingId: Int,
    var userId: String,
    var employeeId: Int,
    var plat: String = "",
    var isParked: Boolean = false,
)
