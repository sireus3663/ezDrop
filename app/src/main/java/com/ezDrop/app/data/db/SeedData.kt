package com.ezDrop.app.data.db

import com.ezDrop.app.data.db.entity.CaseEntity
import com.ezDrop.app.data.db.entity.CaseItemEntity
import com.ezDrop.app.data.db.entity.ItemEntity

object SeedData {

    val items = listOf(
        ItemEntity(name = "Glock-18 | Sand Dune", rarity = "common", category = "pistol", imageRes = "glock_sand_dune", basePrice = 50),
        ItemEntity(name = "P250 | Boreal Forest", rarity = "common", category = "pistol", imageRes = "p250_boreal", basePrice = 60),
        ItemEntity(name = "SSG 08 | Jungle Dashed", rarity = "common", category = "sniper", imageRes = "ssg_jungle", basePrice = 70),
        ItemEntity(name = "FAMAS | Colony", rarity = "common", category = "rifle", imageRes = "famas_colony", basePrice = 80),
        ItemEntity(name = "MP5-SD | Jungle Slipstream", rarity = "common", category = "smg", imageRes = "mp5_jungle", basePrice = 90),
        ItemEntity(name = "MP9 | Rose Iron", rarity = "uncommon", category = "smg", imageRes = "mp9_rose", basePrice = 200),
        ItemEntity(name = "MAC-10 | Silver", rarity = "uncommon", category = "smg", imageRes = "mac10_silver", basePrice = 250),
        ItemEntity(name = "P2000 | Ivory", rarity = "uncommon", category = "pistol", imageRes = "p2000_ivory", basePrice = 180),
        ItemEntity(name = "UMP-45 | Carbon Fiber", rarity = "uncommon", category = "smg", imageRes = "ump_carbon", basePrice = 220),
        ItemEntity(name = "M249 | Magma", rarity = "uncommon", category = "heavy", imageRes = "m249_magma", basePrice = 150),
        ItemEntity(name = "AK-47 | Redline", rarity = "rare", category = "rifle", imageRes = "ak_redline", basePrice = 800),
        ItemEntity(name = "M4A1-S | Blood Tiger", rarity = "rare", category = "rifle", imageRes = "m4a1s_blood", basePrice = 600),
        ItemEntity(name = "AWP | Worm God", rarity = "rare", category = "sniper", imageRes = "awp_worm", basePrice = 700),
        ItemEntity(name = "USP-S | Guardian", rarity = "rare", category = "pistol", imageRes = "usps_guardian", basePrice = 500),
        ItemEntity(name = "M4A4 | Asiimov", rarity = "epic", category = "rifle", imageRes = "m4a4_asiimov", basePrice = 3000),
        ItemEntity(name = "AWP | Hyper Beast", rarity = "epic", category = "sniper", imageRes = "awp_hyper", basePrice = 2500),
        ItemEntity(name = "Desert Eagle | Blaze", rarity = "epic", category = "pistol", imageRes = "deagle_blaze", basePrice = 2000),
        ItemEntity(name = "AWP | Dragon Lore", rarity = "legendary", category = "sniper", imageRes = "awp_dlore", basePrice = 15000),
        ItemEntity(name = "Karambit | Fade", rarity = "legendary", category = "knife", imageRes = "kara_fade", basePrice = 12000),
    )

    private data class CaseSlot(val itemIndex: Int, val dropWeight: Int)

    private data class CaseDef(
        val name: String,
        val imageRes: String,
        val price: Int,
        val requiredLevel: Int,
        val slots: List<CaseSlot>
    )

    private val caseDefs = listOf(
        CaseDef(
            name = "Starter Case",
            imageRes = "img",
            price = 100,
            requiredLevel = 1,
            slots = listOf(
                CaseSlot(0, 400), CaseSlot(1, 400), CaseSlot(2, 400), CaseSlot(3, 400), CaseSlot(4, 400),
                CaseSlot(5, 150), CaseSlot(6, 150), CaseSlot(7, 150),
                CaseSlot(10, 20),
            )
        ),
        CaseDef(
            name = "Premium Case",
            imageRes = "img",
            price = 500,
            requiredLevel = 5,
            slots = listOf(
                CaseSlot(8, 250), CaseSlot(9, 250),
                CaseSlot(10, 80), CaseSlot(11, 80), CaseSlot(12, 80), CaseSlot(13, 80),
                CaseSlot(14, 25), CaseSlot(15, 25),
            )
        ),
        CaseDef(
            name = "Legendary Vault",
            imageRes = "img",
            price = 1500,
            requiredLevel = 10,
            slots = listOf(
                CaseSlot(11, 120),
                CaseSlot(14, 40), CaseSlot(15, 40),
                CaseSlot(17, 8), CaseSlot(18, 8),
            )
        ),
    )

    val cases: List<CaseEntity>
        get() = caseDefs.map { def ->
            CaseEntity(name = def.name, imageRes = def.imageRes, price = def.price, requiredLevel = def.requiredLevel)
        }

    fun buildCaseItems(itemIds: List<Long>, caseIds: List<Long>): List<CaseItemEntity> {
        return caseDefs.flatMapIndexed { caseIdx, def ->
            def.slots.map { slot ->
                CaseItemEntity(
                    caseId = caseIds[caseIdx],
                    itemId = itemIds[slot.itemIndex],
                    dropWeight = slot.dropWeight
                )
            }
        }
    }
}
