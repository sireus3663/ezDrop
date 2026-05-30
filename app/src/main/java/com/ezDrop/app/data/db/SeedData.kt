package com.ezDrop.app.data.db

import com.ezDrop.app.data.db.entity.CaseEntity
import com.ezDrop.app.data.db.entity.CaseItemEntity
import com.ezDrop.app.data.db.entity.ItemEntity

object SeedData {

    val items = listOf(
        ItemEntity(name = "Glock-18 | Sand Dune", rarity = "common", quality = "Field-Tested", category = "pistol", imageRes = "glock_sand_dune"),
        ItemEntity(name = "P250 | Boreal Forest", rarity = "common", quality = "Field-Tested", category = "pistol", imageRes = "p250_boreal"),
        ItemEntity(name = "SSG 08 | Jungle Dashed", rarity = "common", quality = "Well-Worn", category = "sniper", imageRes = "ssg_jungle"),
        ItemEntity(name = "FAMAS | Colony", rarity = "common", quality = "Battle-Scarred", category = "rifle", imageRes = "famas_colony"),
        ItemEntity(name = "MP5-SD | Jungle Slipstream", rarity = "common", quality = "Field-Tested", category = "smg", imageRes = "mp5_jungle"),
        ItemEntity(name = "MP9 | Rose Iron", rarity = "uncommon", quality = "Minimal Wear", category = "smg", imageRes = "mp9_rose"),
        ItemEntity(name = "MAC-10 | Silver", rarity = "uncommon", quality = "Factory New", category = "smg", imageRes = "mac10_silver"),
        ItemEntity(name = "P2000 | Ivory", rarity = "uncommon", quality = "Minimal Wear", category = "pistol", imageRes = "p2000_ivory"),
        ItemEntity(name = "UMP-45 | Carbon Fiber", rarity = "uncommon", quality = "Field-Tested", category = "smg", imageRes = "ump_carbon"),
        ItemEntity(name = "M249 | Magma", rarity = "uncommon", quality = "Well-Worn", category = "heavy", imageRes = "m249_magma"),
        ItemEntity(name = "AK-47 | Redline", rarity = "rare", quality = "Field-Tested", category = "rifle", imageRes = "ak_redline"),
        ItemEntity(name = "M4A1-S | Blood Tiger", rarity = "rare", quality = "Minimal Wear", category = "rifle", imageRes = "m4a1s_blood"),
        ItemEntity(name = "AWP | Worm God", rarity = "rare", quality = "Field-Tested", category = "sniper", imageRes = "awp_worm"),
        ItemEntity(name = "USP-S | Guardian", rarity = "rare", quality = "Factory New", category = "pistol", imageRes = "usps_guardian"),
        ItemEntity(name = "M4A4 | Asiimov", rarity = "epic", quality = "Field-Tested", category = "rifle", imageRes = "m4a4_asiimov"),
        ItemEntity(name = "AWP | Hyper Beast", rarity = "epic", quality = "Minimal Wear", category = "sniper", imageRes = "awp_hyper"),
        ItemEntity(name = "Desert Eagle | Blaze", rarity = "epic", quality = "Factory New", category = "pistol", imageRes = "deagle_blaze"),
        ItemEntity(name = "AWP | Dragon Lore", rarity = "legendary", quality = "Factory New", category = "sniper", imageRes = "awp_dlore"),
        ItemEntity(name = "Karambit | Fade", rarity = "legendary", quality = "Factory New", category = "knife", imageRes = "kara_fade"),
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
            imageRes = "starter_case",
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
            imageRes = "premium_case",
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
            imageRes = "legendary_vault",
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
