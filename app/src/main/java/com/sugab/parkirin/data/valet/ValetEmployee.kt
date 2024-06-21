package com.sugab.parkirin.data.valet

data class ValetEmployee(
    val id: Int,
    var name: String,
    var isReady: Boolean = true,
) {
    // No-argument constructor for Firestore
    constructor() : this(0, "", false)
}
