package com.example.data.model

data class OwnerPaymentConfig(
    val jazzCashName: String = "Hassan",
    val jazzCashNumber: String = "",
    val easypaisaName: String = "Hassan",
    val easypaisaNumber: String = "",
    val bankName: String = "",
    val bankIban: String = "",
    val whatsappNumber: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "jazzCashName" to jazzCashName,
        "jazzCashNumber" to jazzCashNumber,
        "easypaisaName" to easypaisaName,
        "easypaisaNumber" to easypaisaNumber,
        "bankName" to bankName,
        "bankIban" to bankIban,
        "whatsappNumber" to whatsappNumber
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): OwnerPaymentConfig {
            return OwnerPaymentConfig(
                jazzCashName = (map["jazzCashName"] as? String) ?: "Hassan",
                jazzCashNumber = (map["jazzCashNumber"] as? String) ?: "",
                easypaisaName = (map["easypaisaName"] as? String) ?: "Hassan",
                easypaisaNumber = (map["easypaisaNumber"] as? String) ?: "",
                bankName = (map["bankName"] as? String) ?: "",
                bankIban = (map["bankIban"] as? String) ?: "",
                whatsappNumber = (map["whatsappNumber"] as? String) ?: ""
            )
        }
    }
}
