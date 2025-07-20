// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

final class Skills {

    // Total number of skills defined
    public static final int skillsCount = 25;

    // Names of each skill, indexed by ID
    public static final String[] skillNames = {
        "attack",      // 0
        "defence",     // 1
        "strength",    // 2
        "hitpoints",   // 3
        "ranged",      // 4
        "prayer",      // 5
        "magic",       // 6
        "cooking",     // 7
        "woodcutting", // 8
        "fletching",   // 9
        "fishing",     // 10
        "firemaking",  // 11
        "crafting",    // 12
        "smithing",    // 13
        "mining",      // 14
        "herblore",    // 15
        "agility",     // 16
        "thieving",    // 17
        "slayer",      // 18
        "farming",     // 19
        "runecraft",   // 20
        "-unused-",    // 21
        "-unused-",    // 22
        "-unused-",    // 23
        "-unused-"     // 24
    };

    // Whether each skill is enabled or not
    public static final boolean[] skillEnabled = {
        true,  // attack
        true,  // defence
        true,  // strength
        true,  // hitpoints
        true,  // ranged
        true,  // prayer
        true,  // magic
        true,  // cooking
        true,  // woodcutting
        true,  // fletching
        true,  // fishing
        true,  // firemaking
        true,  // crafting
        true,  // smithing
        true,  // mining
        true,  // herblore
        true,  // agility
        true,  // thieving
        true,  // slayer
        false, // farming (disabled)
        true,  // runecraft
        false, // unused
        false, // unused
        false, // unused
        false  // unused
    };

}
