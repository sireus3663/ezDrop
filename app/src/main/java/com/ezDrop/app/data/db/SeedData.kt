package com.ezDrop.app.data.db

import com.ezDrop.app.data.db.entity.CaseEntity
import com.ezDrop.app.data.db.entity.CaseItemEntity
import com.ezDrop.app.data.db.entity.ItemEntity

object SeedData {

    val items = listOf(
        ItemEntity(name = "Glock-18 | Sand Dune", rarity = "common", category = "pistol", imageRes = "glock_sand_dune", basePrice = 50),
        ItemEntity(name = "P250 | Boreal Forest", rarity = "uncommon", category = "pistol", imageRes = "p250_boreal", basePrice = 200),
        ItemEntity(name = "SSG 08 | Jungle Dashed", rarity = "uncommon", category = "sniper", imageRes = "ssg_jungle", basePrice = 300),
        ItemEntity(name = "M249 | Magma", rarity = "common", category = "heavy", imageRes = "m249_magma", basePrice = 100),
        ItemEntity(name = "AWP | Dragon Lore", rarity = "legendary", category = "sniper", imageRes = "awp_dlore", basePrice = 25000),
        ItemEntity(name = "AK-47 | Safari Mesh", rarity = "common", category = "rifle", imageRes = "ak_safari", basePrice = 40),
        ItemEntity(name = "G3SG1 | Safari Mesh", rarity = "common", category = "sniper", imageRes = "g3_safari", basePrice = 45),
        ItemEntity(name = "Nova | Rust Coat", rarity = "common", category = "heavy", imageRes = "nova_rust", basePrice = 60),
        ItemEntity(name = "SSG 08 | Sand Dune", rarity = "common", category = "sniper", imageRes = "ssg_sand", basePrice = 80),
        ItemEntity(name = "AWP | Safari Mesh", rarity = "rare", category = "sniper", imageRes = "awp_safari", basePrice = 800),
        ItemEntity(name = "Bayonet | Safari Mesh", rarity = "legendary", category = "knife", imageRes = "bayonet_safari", basePrice = 5000),
        ItemEntity(name = "Nova | Wild Six", rarity = "common", category = "heavy", imageRes = "nova_wild6", basePrice = 120),
        ItemEntity(name = "PP-Bizon | Fuel Rod", rarity = "common", category = "smg", imageRes = "bizon_fuel", basePrice = 120),
        ItemEntity(name = "P90 | Death Grip", rarity = "common", category = "smg", imageRes = "p90_death", basePrice = 150),
        ItemEntity(name = "Sawed-Off | Highwayman", rarity = "uncommon", category = "heavy", imageRes = "sawed_highway", basePrice = 200),
        ItemEntity(name = "MP9 | Deadly Poison", rarity = "uncommon", category = "smg", imageRes = "mp9_poison", basePrice = 250),
        ItemEntity(name = "R8 Revolver | Crimson Web", rarity = "uncommon", category = "pistol", imageRes = "r8_crimson", basePrice = 400),
        ItemEntity(name = "AWP | Duality", rarity = "rare", category = "sniper", imageRes = "awp_duality", basePrice = 600),
        ItemEntity(name = "USP-S | Stainless", rarity = "rare", category = "pistol", imageRes = "usps_stainless", basePrice = 500),
        ItemEntity(name = "M4A4 | Desolate Space", rarity = "epic", category = "rifle", imageRes = "m4a4_space", basePrice = 3000),
        ItemEntity(name = "Karambit | Crimson Web", rarity = "legendary", category = "knife", imageRes = "kara_crimson", basePrice = 7000),
        ItemEntity(name = "MAC-10 | Disco Tech", rarity = "common", category = "smg", imageRes = "mac10_disco", basePrice = 600),
        ItemEntity(name = "FAMAS | ZX Spectron", rarity = "common", category = "rifle", imageRes = "famas_zx", basePrice = 600),
        ItemEntity(name = "Five-SeveN | Violent Daimyo", rarity = "common", category = "pistol", imageRes = "fiveseven_violent", basePrice = 650),
        ItemEntity(name = "UMP-45 | Neo-Noir", rarity = "uncommon", category = "smg", imageRes = "ump_neonoir", basePrice = 1000),
        ItemEntity(name = "MP7 | Bloodsport", rarity = "uncommon", category = "smg", imageRes = "mp7_bloodsport", basePrice = 1200),
        ItemEntity(name = "Galil AR | Sugar Rush", rarity = "uncommon", category = "rifle", imageRes = "galil_sugar", basePrice = 1000),
        ItemEntity(name = "AK-47 | Point Disarray", rarity = "rare", category = "rifle", imageRes = "ak_point", basePrice = 2000),
        ItemEntity(name = "M4A1-S | Cyrex", rarity = "rare", category = "rifle", imageRes = "m4a1s_cyrex", basePrice = 2500),
        ItemEntity(name = "AWP | Fever Dream", rarity = "epic", category = "sniper", imageRes = "awp_fever", basePrice = 6000),
        ItemEntity(name = "Flip Knife | Freehand", rarity = "legendary", category = "knife", imageRes = "flip_freehand", basePrice = 12000),
        ItemEntity(name = "P2000 | Obsidian", rarity = "common", category = "pistol", imageRes = "p2000_obsidian", basePrice = 2000),
        ItemEntity(name = "P250 | Contaminant", rarity = "common", category = "pistol", imageRes = "p250_contam", basePrice = 1800),
        ItemEntity(name = "MP7 | Armor Core", rarity = "common", category = "smg", imageRes = "mp7_armor", basePrice = 2000),
        ItemEntity(name = "USP-S | Lead Conduit", rarity = "common", category = "pistol", imageRes = "usps_lead", basePrice = 2500),
        ItemEntity(name = "AUG | Plague", rarity = "uncommon", category = "rifle", imageRes = "aug_plague", basePrice = 3500),
        ItemEntity(name = "MAC-10 | Stalker", rarity = "uncommon", category = "smg", imageRes = "mac10_stalker", basePrice = 3000),
        ItemEntity(name = "SSG 08 | Death Strike", rarity = "rare", category = "sniper", imageRes = "ssg_death", basePrice = 5000),
        ItemEntity(name = "AK-47 | Wasteland Rebel", rarity = "rare", category = "rifle", imageRes = "ak_rebel", basePrice = 6000),
        ItemEntity(name = "AWP | Neo-Noir", rarity = "epic", category = "sniper", imageRes = "awp_neonoir", basePrice = 9000),
        ItemEntity(name = "Butterfly Knife | Night", rarity = "legendary", category = "knife", imageRes = "butterfly_night", basePrice = 15000),
        ItemEntity(name = "M9 Bayonet | Ultraviolet", rarity = "legendary", category = "knife", imageRes = "m9_ultra", basePrice = 12000),
        ItemEntity(name = "Tec-9 | Decimator", rarity = "uncommon", category = "pistol", imageRes = "tec9_decim", basePrice = 8000),
        ItemEntity(name = "P250 | Valence", rarity = "uncommon", category = "pistol", imageRes = "p250_valence", basePrice = 7000),
        ItemEntity(name = "MP9 | Bioleak", rarity = "uncommon", category = "smg", imageRes = "mp9_bioleak", basePrice = 7000),
        ItemEntity(name = "AK-47 | Phantom Disruptor", rarity = "rare", category = "rifle", imageRes = "ak_phantom", basePrice = 12000),
        ItemEntity(name = "M4A1-S | Mecha Industries", rarity = "rare", category = "rifle", imageRes = "m4a1s_mecha", basePrice = 12000),
        ItemEntity(name = "AWP | Exoskeleton", rarity = "rare", category = "sniper", imageRes = "awp_exo", basePrice = 10000),
        ItemEntity(name = "Desert Eagle | Mecha Industries", rarity = "epic", category = "pistol", imageRes = "deagle_mecha", basePrice = 16000),
        ItemEntity(name = "Five-SeveN | Hyper Beast", rarity = "epic", category = "pistol", imageRes = "fiveseven_hyper", basePrice = 15000),
        ItemEntity(name = "FAMAS | Mecha Industries", rarity = "epic", category = "rifle", imageRes = "famas_mecha", basePrice = 15000),
        ItemEntity(name = "Karambit | Stained", rarity = "legendary", category = "knife", imageRes = "kara_stained", basePrice = 18000),
        ItemEntity(name = "Bayonet | Autotronic", rarity = "legendary", category = "knife", imageRes = "bayonet_auto", basePrice = 20000),
        ItemEntity(name = "M9 Bayonet | Lore", rarity = "legendary", category = "knife", imageRes = "m9_lore", basePrice = 22000),
        ItemEntity(name = "Karambit | Gamma Doppler", rarity = "legendary", category = "knife", imageRes = "kara_gamma", basePrice = 15000),
        ItemEntity(name = "Souvenir Five-SeveN | Hyper Beast", rarity = "souvenir", category = "pistol", imageRes = "souv_57_hyper", basePrice = 250),
        ItemEntity(name = "Souvenir P90 | Asiimov", rarity = "souvenir", category = "smg", imageRes = "souv_p90_asiimov", basePrice = 250),
        ItemEntity(name = "Souvenir USP-S | Kill Confirmed", rarity = "souvenir", category = "pistol", imageRes = "souv_usps_kill", basePrice = 500),
        ItemEntity(name = "Souvenir M4A1-S | Hyper Beast", rarity = "souvenir", category = "rifle", imageRes = "souv_m4a1s_hyper", basePrice = 600),
        ItemEntity(name = "Souvenir AK-47 | Point Disarray", rarity = "souvenir", category = "rifle", imageRes = "souv_ak_point", basePrice = 700),
        ItemEntity(name = "Souvenir AWP | Neo-Noir", rarity = "souvenir", category = "sniper", imageRes = "souv_awp_neonoir", basePrice = 2000),
        ItemEntity(name = "Souvenir AWP | Hyper Beast", rarity = "souvenir", category = "sniper", imageRes = "souv_awp_hyper", basePrice = 2500),
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
            name = "African Style",
            imageRes = "img",
            price = 50,
            requiredLevel = 1,
            slots = listOf(
                CaseSlot(5, 2000), CaseSlot(6, 1920), CaseSlot(0, 1920),
                CaseSlot(7, 1820), CaseSlot(8, 1820),
                CaseSlot(1, 230), CaseSlot(2, 150),
                CaseSlot(9, 125),
                CaseSlot(10, 15),
            )
        ),
        CaseDef(
            name = "Crimson Strike",
            imageRes = "img",
            price = 300,
            requiredLevel = 5,
            slots = listOf(
                CaseSlot(11, 1900), CaseSlot(12, 1800), CaseSlot(13, 1800), CaseSlot(3, 1800),
                CaseSlot(14, 700), CaseSlot(15, 650), CaseSlot(16, 650),
                CaseSlot(17, 250), CaseSlot(18, 250),
                CaseSlot(19, 120),
                CaseSlot(20, 80),
            )
        ),
        CaseDef(
            name = "Neon Horizon",
            imageRes = "img",
            price = 1000,
            requiredLevel = 8,
            slots = listOf(
                CaseSlot(21, 2400), CaseSlot(22, 2300), CaseSlot(23, 2200),
                CaseSlot(24, 1300), CaseSlot(25, 800), CaseSlot(26, 500),
                CaseSlot(27, 280), CaseSlot(28, 150),
                CaseSlot(29, 50),
                CaseSlot(30, 20),
            )
        ),
        CaseDef(
            name = "Dark Water",
            imageRes = "img",
            price = 4000,
            requiredLevel = 12,
            slots = listOf(
                CaseSlot(31, 1400), CaseSlot(32, 1400), CaseSlot(33, 1400), CaseSlot(34, 1300),
                CaseSlot(35, 1200), CaseSlot(36, 1100),
                CaseSlot(37, 600), CaseSlot(38, 600),
                CaseSlot(39, 500),
                CaseSlot(40, 200), CaseSlot(41, 200),
            )
        ),
        CaseDef(
            name = "Armory Vault",
            imageRes = "img",
            price = 16000,
            requiredLevel = 20,
            slots = listOf(
                CaseSlot(42, 925), CaseSlot(43, 925), CaseSlot(44, 875),
                CaseSlot(45, 875), CaseSlot(46, 875), CaseSlot(47, 875),
                CaseSlot(48, 775), CaseSlot(49, 775), CaseSlot(50, 675),
                CaseSlot(4, 300),
                CaseSlot(51, 775), CaseSlot(52, 675), CaseSlot(53, 675),
            )
        ),
        CaseDef(
            name = "Second Chance",
            imageRes = "img",
            price = 0,
            requiredLevel = 1,
            slots = listOf(
                CaseSlot(54, 100),
                CaseSlot(55, 2500),
                CaseSlot(56, 2500),
                CaseSlot(57, 1630),
                CaseSlot(58, 1360),
                CaseSlot(59, 1170),
                CaseSlot(60, 410),
                CaseSlot(61, 330),
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
