package com.ezDrop.app.data.util

// BALANCE: adjust these thresholds (CS:GO standard)
fun wearTier(float: Float): String = when {
    float < 0.07f -> "Factory New"
    float < 0.15f -> "Minimal Wear"
    float < 0.38f -> "Field-Tested"
    float < 0.45f -> "Well-Worn"
    else -> "Battle-Scarred"
}

// BALANCE: adjust these multipliers
fun wearMultiplier(tier: String): Float = when (tier) {
    "Factory New" -> 1.5f
    "Minimal Wear" -> 1.2f
    "Field-Tested" -> 1.0f
    "Well-Worn" -> 0.75f
    "Battle-Scarred" -> 0.4f
    else -> 1.0f
}

fun floatToPrice(basePrice: Int, wearFloat: Float): Int {
    return (basePrice * wearMultiplier(wearTier(wearFloat))).toInt()
}
