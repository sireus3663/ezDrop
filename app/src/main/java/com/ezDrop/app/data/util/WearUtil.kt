package com.ezDrop.app.data.util

// BALANCE: adjust these thresholds (CS:GO standard)
fun wearTier(float: Float): String = when {
    float < 0.1f -> "Factory New"
    float < 0.3f -> "Minimal Wear"
    float < 0.6f -> "Field-Tested"
    float < 0.8f -> "Well-Worn"
    else -> "Battle-Scarred"
}

// BALANCE: adjust these multipliers
fun wearMultiplier(tier: String): Float = when (tier) {
    "Factory New" -> 2.5f
    "Minimal Wear" -> 1.5f
    "Field-Tested" -> 1.0f
    "Well-Worn" -> 0.75f
    "Battle-Scarred" -> 0.4f
    else -> 1.0f
}

fun floatToPrice(basePrice: Int, wearFloat: Float): Int {
    return (basePrice * wearMultiplier(wearTier(wearFloat))).toInt()
}
