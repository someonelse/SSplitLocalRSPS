package server.model.players;

/**
 * Handles the skill menu interface for displaying required items and levels.
 * 
 * @author Sanity
 */
public class SkillMenu {

    private static final int INTERFACE_ID = 8714;
    private static final int LEVEL_LINE = 8720;
    private static final int TEXT_LINE = 8760;
    private static final int TITLE_LINE = 8716;

    // Item IDs for each skill menu category
    private static final int[][] items = {
        {1321,1323,1325,1327,1329,1331,1333,4153,4587,4151,4718,11694,9747}, // Attack
        {1117,1115,1119,1125,1121,6916,1123,1127,3751,2513,10348,11724,11720,4720,11283,9753}, // Defence
        {4153,6528,9750}, // Strength
        {9768}, // Hitpoints
        {841,843,849,853,857,1135,861,2499,11235,6522,2501,9185,10330,4214,2503,4734,9756}, // Ranged
        {9759}, // Prayer
        {4099,6916,6889,7401,3387,4675,10338,4712} // Magic
    };

    // Required levels for each item above
    private static final String[][] LEVELS = {
        {"1","1","5","10","20","30","40","50","60","70","70","75","99"},
        {"1","1","5","10","20","25","30","40","45","60","65","65","70","70","75","99"},
        {"50","60","99"},
        {"99"},
        {"1","5","20","30","30","40","50","50","60","60","60","61","65","70","70","70","99"},
        {"99"},
        {"20","25","25","40","40","50","65","70"}
    };

    // Descriptions of each item
    private static final String[][] DESCRIPTION = {
        {"Bronze Weapons", "Iron Weapons", "Steel Weapons", "Black Weapons", 
         "Mithril Weapons", "Adamant Weapons", "Rune Weapons", "Granite Maul", "Dragon Weapons", "Abyssal Whip", "Barrows Weapons",
         "Godswords", "Cape of Achievement"},
        {"Bronze Armour", "Iron Armour", "Steel Armour", "Black Armour", "Mithril Armour", "Infinity", "Adamant Armour", "Rune Armour", 
         "Fremennik Helmets", "Dragon Armour", "3rd Age Armour", "Bandos", "Armadyl", "Barrows Armour","Dragonfire Shield", "Cape of Achievement"},
        {"Granite Items", "Obby Maul", "Cape of Achievement"},
        {"Cape of Achievement"},
        {"Normal Bows","Oak Bows", "Willow Bows", "Maple Bows","Yew Bows", "Green D'hide", "Magic Bows", "Blue D'hide","Dark Bow","Obby Ring",
         "Red D'hide","Rune C'bow","3rd age Range","Crystal Bow","Black D'hide","Karil's","Cape of Achievement"},
        {"Cape of Achievement"},
        {"Mystic ","Infinity ","Mage's book","Enchanted ","Splitbark ","Ancient staff","3rd age mage","Ahrims"}
    };

    // Skill category names
    private static final String[] SKILLS = {"Attack","Defence","Strength","Hitpoints","Ranged","Prayer","Magic"};

    /*
     * Additional skill documentation kept below for reference (cooking, fishing, woodcutting, mining, runecrafting)
     * 
     * -- Cooking --
     * Item IDs: 317,335,331,359,377,371,383,389,395,9801
     * Levels: 1,15,25,30,40,45,80,91
     * Descriptions: "Shrimp", "Trout", "Salmon", "Tuna", "Lobster", "Swordfish", "Shark", "Manta Ray","Cape of Achievement"
     * 
     * -- Fishing --
     * Item IDs: 317,335,331,359,377,371,383,389,395,9801
     * Levels: 1,20,30,35,40,50,76,81
     * Descriptions: "Shrimp", "Trout", "Salmon", "Tuna", "Lobster", "Swordfish", "Shark", "Manta Ray","Cape of Achievement"
     * 
     * -- Woodcutting --
     * Item IDs: 1351,1349,1511,1353,1521,1355,1519,1357,1517,1515,1359,1513,7797
     * Levels: "1","1","1","6","15","21","30","31","41","45","60","61","75","99"
     * Descriptions: "Bronze Axe","Iron Axe","Logs","Steel Axe","Oak Logs","Mithril Axe","Willow Logs","Adamant Axe","Rune Axe","Maple Logs","Yew Logs","Dragon Axe","Magic Logs","Cape of Achievement"
     * 
     * -- Mining --
     * Item IDs: 1265,1267,1436,434,436,1269,440,442,1273,453,1271,444,1275,447,449,451,9792
     * Levels: "1","1","1","1","1","1","6","15","20","21","30","31","40","41","65","70","85","99"
     * Descriptions: "Bronze pickaxe","Iron pickaxe","Rune essence","Clay","Copper","Tin","steel pickaxe","Iron ore","Silver ore","Mithril pickaxe","Coal ore","Adamant pickaxe","Gold ore","Rune pickaxe","Mithril ore","Adamanetite ore","Runite ore"
     * 
     * -- Runecrafting --
     * Item IDs: 556,558,555,557,554,559,564,562,9075,561,563,560,565
     * Levels: "1","2","5","9","14","20","27","35","40","44","54","65","77"
     * Descriptions: "Air rune","Mind rune","Water rune","Earth rune","Fire rune","Body rune","Cosmic rune","Chaos rune","Astral rune","Nature rune","Law rune","Death rune","Blood rune"
     */

    /** Opens the skill menu interface for the specified skill type. */
    public static void openInterface(Client c, int skillType) {
        removeSidebars(c);
        writeItems(c, skillType);
        writeText(c, skillType);
        c.getPA().showInterface(INTERFACE_ID);
    }

    /** Removes sidebar text lines. */
    private static void removeSidebars(Client c) {
        int[] temp = {8849,8846,8823,8824,8827,8837,8840,8843,8859,8862,8865,15303,15306,15309};
        for (int j = 0; j < temp.length; j++) {
            c.getPA().sendFrame126("", temp[j]);
        }
    }

    /** Writes the items for the skill menu to the interface. */
    private static void writeItems(Client c, int skillType) {
        synchronized (c) {
            c.outStream.createFrameVarSizeWord(53);
            c.outStream.writeWord(8847);
            c.outStream.writeWord(items[skillType].length);
            for (int j = 0; j < items[skillType].length; j++) {
                c.outStream.writeByte(1);
                if (items[skillType][j] > 0) {
                    c.outStream.writeWordBigEndianA(items[skillType][j] + 1);
                } else {
                    c.outStream.writeWordBigEndianA(0);
                }
            }
            c.outStream.endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    /** Writes the level and description text for the skill menu. */
    private static void writeText(Client c, int skillType) {
        c.getPA().sendFrame126(SKILLS[skillType], TITLE_LINE);
        for (int j = 0; j < LEVELS[skillType].length; j++) {
            c.getPA().sendFrame126(LEVELS[skillType][j], LEVEL_LINE + j);
        }
        for (int j = 0; j < DESCRIPTION[skillType].length; j++) {
            c.getPA().sendFrame126(DESCRIPTION[skillType][j], TEXT_LINE + j);
        }
        for (int j = DESCRIPTION[skillType].length; j < 30; j++) {
            c.getPA().sendFrame126("", LEVEL_LINE + j);
        }
        for (int j = LEVELS[skillType].length; j < 30; j++) {
            c.getPA().sendFrame126("", TEXT_LINE + j);
        }
    }
}
