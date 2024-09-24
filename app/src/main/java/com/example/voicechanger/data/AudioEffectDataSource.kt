package com.example.voicechanger.data

import android.content.Context
import com.example.voicechanger.R
import com.example.voicechanger.model.AudioEffectModel

class AudioEffectDataSource(
    private val context: Context
) {
    fun getAllEffect(): List<AudioEffectModel> = listOf(
        AudioEffectModel(
            1,
            context.getString(R.string.normal),
            "normal",
            R.mipmap.ic_normal_unselected,
            R.mipmap.ic_normal_unselected,
            0,
            true
        ),
        AudioEffectModel(
            7,
            context.getString(R.string.drunk),
            "drunk",
            R.mipmap.ic_drunk_unselected,
            R.mipmap.ic_drunk_unselected,
            0,
            false
        ),
        AudioEffectModel(
            11,
            context.getString(R.string.reverse),
            "reverse",
            R.mipmap.ic_reverse_unselected,
            R.mipmap.ic_reverse_unselected,
            0,
            false
        ),
        AudioEffectModel(
            19,
            context.getString(R.string.zombie),
            "zombie",
            R.mipmap.ic_zombie_unselected,
            R.mipmap.ic_zombie_unselected,
            0,
            false
        ),
        AudioEffectModel(
            3,
            context.getString(R.string.robot),
            "robot",
            R.mipmap.ic_roboto_unselected,
            R.mipmap.ic_roboto_unselected,
            0,
            false
        ),
        AudioEffectModel(
            6,
            context.getString(R.string.nervous),
            "nervous",
            R.mipmap.ic_nervous_unselected,
            R.mipmap.ic_nervous_unselected,
            0,
            false
        ),
        AudioEffectModel(
            10,
            context.getString(R.string.death),
            "death",
            R.mipmap.ic_death_unselected,
            R.mipmap.ic_death_unselected,
            0,
            false
        ),
        AudioEffectModel(
            2,
            context.getString(R.string.helium),
            "helium",
            R.mipmap.ic_helium_unselected,
            R.mipmap.ic_helium_unselected,
            0,
            false
        ),
        AudioEffectModel(
            8,
            context.getString(R.string.squirrel),
            "squirrel",
            R.mipmap.ic_squirrel_unselected,
            R.mipmap.ic_squirrel_unselected,
            0,
            false
        ),
        AudioEffectModel(
            5,
            context.getString(R.string.monster),
            "monster",
            R.mipmap.ic_monster_unselected,
            R.mipmap.ic_monster_unselected,
            0,
            false
        ),
        AudioEffectModel(
            14,
            context.getString(R.string.big_robot),
            "bigrobot",
            R.mipmap.ic_big_roboto_unselected,
            R.mipmap.ic_big_roboto_unselected,
            0,
            false
        ),
        AudioEffectModel(
            21,
            context.getString(R.string.alien),
            "alien",
            R.mipmap.ic_alien_unselected,
            R.mipmap.ic_alien_unselected,
            0,
            false
        ),
        AudioEffectModel(
            9,
            context.getString(R.string.child),
            "child",
            R.mipmap.ic_child_unselected,
            R.mipmap.ic_child_unselected,
            0,
            false
        ),
        AudioEffectModel(
            16,
            context.getString(R.string.underwater),
            "underwater",
            R.mipmap.ic_underwater_unselected,
            R.mipmap.ic_underwater_unselected,
            0,
            false
        ),
        AudioEffectModel(
            13,
            context.getString(R.string.hexafluoride),
            "hexafluoride",
            R.mipmap.ic_hexafluoride_unselected,
            R.mipmap.ic_hexafluoride_unselected,
            0,
            false
        ),
        AudioEffectModel(
            22,
            context.getString(R.string.small_alien),
            "smallalien",
            R.mipmap.back_chimp_unseleted,
            R.mipmap.back_chimp_unseleted,
            0,
            false
        ),
        AudioEffectModel(
            15,
            context.getString(R.string.telephone),
            "telephone",
            R.mipmap.ic_telephone_unselected,
            R.mipmap.ic_telephone_unselected,
            0,
            false
        ),
        AudioEffectModel(
            17,
            context.getString(R.string.extraterrestrial),
            "extraterrestrial",
            R.mipmap.ic_extraterrestrial_unselected,
            R.mipmap.ic_extraterrestrial_unselected,
            0,
            false
        ),
        AudioEffectModel(
            4,
            context.getString(R.string.cave),
            "cave",
            R.mipmap.ic_cave_unselected,
            R.mipmap.ic_cave_unselected,
            0,
            false
        ),
        AudioEffectModel(
            20,
            context.getString(R.string.megaphone),
            "megaphone",
            R.mipmap.ic_megaphone_unselected,
            R.mipmap.ic_megaphone_unselected,
            0,
            false
        ),
        AudioEffectModel(
            18,
            context.getString(R.string.villain),
            "villain",
            R.mipmap.ic_villain_unselected,
            R.mipmap.ic_villain_unselected,
            0,
            false
        ),
        AudioEffectModel(
            23,
            context.getString(R.string.back_chipmunks),
            "backchipmunk",
            R.mipmap.back_chimp_unseleted,
            R.mipmap.back_chimp_unseleted,
            0,
            false
        ),
        AudioEffectModel(
            12,
            context.getString(R.string.grand_canyon),
            "grandcanyon",
            R.mipmap.ic_grand_canyon_unselected,
            R.mipmap.ic_grand_canyon_unselected,
            0,
            false
        )
    )

    fun getRobotEffect(): List<AudioEffectModel> = listOf(
        AudioEffectModel(
            14,
            context.getString(R.string.big_robot),
            "bigrobot",
            R.mipmap.ic_big_roboto_unselected,
            R.mipmap.ic_big_roboto_unselected,
            0,
            false
        ),
        AudioEffectModel(
            3,
            context.getString(R.string.robot),
            "robot",
            R.mipmap.ic_roboto_unselected,
            R.mipmap.ic_roboto_unselected,
            0,
            false
        )
    )

    fun getPeopleEffect(): List<AudioEffectModel> = listOf(
        AudioEffectModel(
            9,
            context.getString(R.string.child),
            "child",
            R.mipmap.ic_child_unselected,
            R.mipmap.ic_child_unselected,
            0,
            false
        ),
        AudioEffectModel(
            6,
            context.getString(R.string.nervous),
            "nervous",
            R.mipmap.ic_nervous_unselected,
            R.mipmap.ic_nervous_unselected,
            0,
            false
        ),
        AudioEffectModel(
            18,
            context.getString(R.string.villain),
            "villain",
            R.mipmap.ic_villain_unselected,
            R.mipmap.ic_villain_unselected,
            0,
            false
        ),
        AudioEffectModel(
            16,
            context.getString(R.string.underwater),
            "underwater",
            R.mipmap.ic_underwater_unselected,
            R.mipmap.ic_underwater_unselected,
            0,
            false
        ),
        AudioEffectModel(
            7,
            context.getString(R.string.drunk),
            "drunk",
            R.mipmap.ic_drunk_unselected,
            R.mipmap.ic_drunk_unselected,
            0,
            false
        )
    )


    fun getScaryEffect(): List<AudioEffectModel> = listOf(
        AudioEffectModel(
            19,
            context.getString(R.string.zombie),
            "zombie",
            R.mipmap.ic_zombie_unselected,
            R.mipmap.ic_zombie_unselected,
            0,
            false
        ),
        AudioEffectModel(
            21,
            context.getString(R.string.alien),
            "alien",
            R.mipmap.ic_alien_unselected,
            R.mipmap.ic_alien_unselected,
            0,
            false
        ),
        AudioEffectModel(
            22,
            context.getString(R.string.small_alien),
            "smallalien",
            R.mipmap.ic_small_alien_unselected,
            R.mipmap.ic_small_alien_unselected,
            0,
            false
        ),
        AudioEffectModel(
            5,
            context.getString(R.string.monster),
            "monster",
            R.mipmap.ic_monster_unselected,
            R.mipmap.ic_monster_unselected,
            0,
            false
        ),
        AudioEffectModel(
            17,
            context.getString(R.string.extraterrestrial),
            "extraterrestrial",
            R.mipmap.ic_extraterrestrial_unselected,
            R.mipmap.ic_extraterrestrial_unselected,
            0,
            false
        ),
        AudioEffectModel(
            10,
            context.getString(R.string.death),
            "death",
            R.mipmap.ic_death_unselected,
            R.mipmap.ic_death_unselected,
            0,
            false
        )
    )

    fun getOtherEffect(): List<AudioEffectModel> = listOf(
        AudioEffectModel(
            4,
            context.getString(R.string.cave),
            "cave",
            R.mipmap.ic_cave_unselected,
            R.mipmap.ic_cave_unselected,
            0,
            false
        ),
        AudioEffectModel(
            20,
            context.getString(R.string.megaphone),
            "megaphone",
            R.mipmap.ic_megaphone_unselected,
            R.mipmap.ic_megaphone_unselected,
            0,
            false
        ),
        AudioEffectModel(
            8,
            context.getString(R.string.squirrel),
            "squirrel",
            R.mipmap.ic_squirrel_unselected,
            R.mipmap.ic_squirrel_unselected,
            0,
            false
        ),
        AudioEffectModel(
            15,
            context.getString(R.string.telephone),
            "telephone",
            R.mipmap.ic_telephone_unselected,
            R.mipmap.ic_telephone_unselected,
            0,
            false
        ),
        AudioEffectModel(
            13,
            context.getString(R.string.hexafluoride),
            "hexafluoride",
            R.mipmap.ic_hexafluoride_unselected,
            R.mipmap.ic_hexafluoride_unselected,
            0,
            false
        ),
        AudioEffectModel(
            12,
            context.getString(R.string.grand_canyon),
            "grandcanyon",
            R.mipmap.ic_grand_canyon_unselected,
            R.mipmap.ic_grand_canyon_unselected,
            0,
            false
        ),
        AudioEffectModel(
            11,
            context.getString(R.string.reverse),
            "reverse",
            R.mipmap.ic_reverse_unselected,
            R.mipmap.ic_reverse_unselected,
            0,
            false
        ),
        AudioEffectModel(
            2,
            context.getString(R.string.helium),
            "helium",
            R.mipmap.ic_helium_unselected,
            R.mipmap.ic_helium_unselected,
            0,
            false
        ),
        AudioEffectModel(
            23,
            context.getString(R.string.back_chipmunks),
            "backchipmunk",
            R.mipmap.back_chimp_unseleted,
            R.mipmap.back_chimp_unseleted,
            0,
            false
        )
    )

    fun getAIEffect() = listOf(
        AudioEffectModel(
            id = 1,
            name = "Trump",
            nameOrigin = "Trump",
            iconSelected = R.mipmap.image1,
            iconUnSelected = R.mipmap.image1,
            thumb = 0,
            isActive = false,
            token = "TM:4v0ft4j72y2g"
        ),
        AudioEffectModel(
            id = 2,
            name = "Obama",
            nameOrigin = "Obama",
            iconSelected = R.mipmap.image2,
            iconUnSelected = R.mipmap.image2,
            thumb = 0,
            isActive = false,
            token = "TM:58vtv7x9f32c"
        ),
        AudioEffectModel(
            id = 3,
            name = "Morgan",
            nameOrigin = "Morgan",
            iconSelected = R.mipmap.image3,
            iconUnSelected = R.mipmap.image3,
            thumb = 0,
            isActive = false,
            token = "TM:xcx5ytjsv8b3"
        ),
        AudioEffectModel(
            id = 4,
            name = "John",
            nameOrigin = "John",
            iconSelected = R.mipmap.image4,
            iconUnSelected = R.mipmap.image4,
            thumb = 0,
            isActive = false,
            token = "TM:fyqkwgdd09ey"
        ),
        AudioEffectModel(
            id = 5,
            name = "Brady",
            nameOrigin = "Brady",
            iconSelected = R.mipmap.image5,
            iconUnSelected = R.mipmap.image5,
            thumb = 0,
            isActive = false,
            token = "TM:fnkmhbrznmeh"
        ),
        AudioEffectModel(
            id = 6,
            name = "A.Tate",
            nameOrigin = "A.Tate",
            iconSelected = R.mipmap.image6,
            iconUnSelected = R.mipmap.image6,
            thumb = 0,
            isActive = false,
            token = "TM:43c7p13p3z5c"
        ),
        AudioEffectModel(
            id = 7,
            name = "Zendaya",
            nameOrigin = "Zendaya",
            iconSelected = R.mipmap.image7,
            iconUnSelected = R.mipmap.image7,
            thumb = 0,
            isActive = false,
            token = "TM:f5hcw922p29b"
        ),
        AudioEffectModel(
            id = 8,
            name = "Eminem",
            nameOrigin = "Eminem",
            iconSelected = R.mipmap.image8,
            iconUnSelected = R.mipmap.image8,
            thumb = 0,
            isActive = false,
            token = "TM:pdf9c1anbdjq"
        ),
        AudioEffectModel(
            id = 9,
            name = "Barbie",
            nameOrigin = "Barbie",
            iconSelected = R.mipmap.image1,
            iconUnSelected = R.mipmap.image1,
            thumb = 0,
            isActive = false,
            token = "TM:1zj2er3hdwhb"
        ),
        AudioEffectModel(
            id = 10,
            name = "2Pac",
            nameOrigin = "2Pac",
            iconSelected = R.mipmap.image1,
            iconUnSelected = R.mipmap.image1,
            thumb = 0,
            isActive = false,
            token = "TM:jv2j06zg7vh0"
        )
    )

}