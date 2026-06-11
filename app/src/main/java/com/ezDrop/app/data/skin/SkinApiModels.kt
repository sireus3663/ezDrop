package com.ezDrop.app.data.skin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSkin(
    val id: String,
    val name: String,
    @SerialName("market_hash_name")
    val marketHashName: String,
    val wear: ApiWear? = null,
    val image: String,
)

@Serializable
data class ApiWear(
    val name: String,
)
