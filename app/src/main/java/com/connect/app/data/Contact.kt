package com.connect.app.data

import java.io.Serializable

data class Contact(
    val id: String = "",
    val name: String = "",
    val surname: String = "",
    val age: Int = 0,
    val gmail: String = "",
    val linkedIn: String = "",
    val github: String = "",
    val facebook: String = "",
    val twitter: String = "",
    val instagram: String = "",
    val website: String = "",
    val phone: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {
    
    fun getFullName(): String = "$name $surname"
}
