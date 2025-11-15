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
    
    fun toJson(): String {
        return """
            {
                "id": "$id",
                "name": "$name",
                "surname": "$surname",
                "age": $age,
                "gmail": "$gmail",
                "linkedIn": "$linkedIn",
                "github": "$github",
                "facebook": "$facebook",
                "twitter": "$twitter",
                "instagram": "$instagram",
                "website": "$website",
                "phone": "$phone",
                "timestamp": $timestamp
            }
        """.trimIndent()
    }
    
    companion object {
        fun fromJson(json: String): Contact? {
            return try {
                // Simple JSON parsing without external libraries
                val data = json.trim()
                    .removePrefix("{")
                    .removeSuffix("}")
                    .split(",")
                    .associate {
                        val parts = it.split(":", limit = 2)
                        if (parts.size == 2) {
                            parts[0].trim().removeSurrounding("\"") to parts[1].trim().removeSurrounding("\"")
                        } else {
                            "" to ""
                        }
                    }
                
                Contact(
                    id = data["id"] ?: "",
                    name = data["name"] ?: "",
                    surname = data["surname"] ?: "",
                    age = data["age"]?.toIntOrNull() ?: 0,
                    gmail = data["gmail"] ?: "",
                    linkedIn = data["linkedIn"] ?: "",
                    github = data["github"] ?: "",
                    facebook = data["facebook"] ?: "",
                    twitter = data["twitter"] ?: "",
                    instagram = data["instagram"] ?: "",
                    website = data["website"] ?: "",
                    phone = data["phone"] ?: "",
                    timestamp = data["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
