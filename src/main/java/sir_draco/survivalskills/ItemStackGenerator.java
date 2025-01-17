package sir_draco.survivalskills;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ItemStackGenerator {

    /**
     * Creates a custom item based on the parameters
     * @param material Item material
     * @param amount Item Stack Quantity
     * @param name Name
     * @param lore Can be NULL, List of lore strings
     * @param modelData Input 0 if no custom model data is necessary
     * @param hideEnchants Whether enchants should be hidden
     * @param enchants Can be NULL, A map of enchants and their level to add to the item
     * @return The Item Stack
     */
    public static ItemStack createCustomItem(Material material, int amount, String name, ChatColor nameColor, String lore, ArrayList<String> loreList, int modelData, boolean hideEnchants, Map<Enchantment, Integer> enchants) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (nameColor != null) name = nameColor + name;
        meta.setDisplayName(name);
        if (lore != null) {
            List<String> loreHolder = new ArrayList<>();
            loreHolder.add(lore);
            meta.setLore(loreHolder);
        }
        if (loreList != null) meta.setLore(loreList);
        if (modelData != 0) meta.setCustomModelData(modelData);
        if (hideEnchants) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(new NamespacedKey(SurvivalSkills.getPlugin(SurvivalSkills.class), "SurvivalSkills"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        if (enchants != null) for (Map.Entry<Enchantment, Integer> enchant : enchants.entrySet()) item.addUnsafeEnchantment(enchant.getKey(), enchant.getValue());
        return item;
    }

    public static void createSmallShapedRecipe(NamespacedKey key, ItemStack result, String shape, ItemStack as, ItemStack bs, ItemStack cs, Material am, Material bm, Material cm) {
        if (getServer().getRecipe(key) != null) getServer().removeRecipe(key);
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        String[] shapes = shape.split(":");

        if (shapes[0].contains("D")) recipe.shape(shapes[1], shapes[2]);
        else if (shapes[2].contains("D")) recipe.shape(shapes[0], shapes[1]);
        else recipe.shape(shapes[0], shapes[1], shapes[2]);

        if (as != null) recipe.setIngredient('A', new RecipeChoice.ExactChoice(as));
        else if (am != null) recipe.setIngredient('A', am);

        if (bs != null) recipe.setIngredient('B', new RecipeChoice.ExactChoice(bs));
        else if (bm != null) recipe.setIngredient('B', bm);

        if (cs != null) recipe.setIngredient('C', new RecipeChoice.ExactChoice(cs));
        else if (cm != null) recipe.setIngredient('C', cm);

        Bukkit.getServer().addRecipe(recipe);
    }

    public static boolean isCustomItem(ItemStack item, int modelData) {
        if (item == null) return false;
        if (item.getItemMeta() == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasCustomModelData()) return false;
        if (!meta.getPersistentDataContainer().has(new NamespacedKey(SurvivalSkills.getPlugin(SurvivalSkills.class), "SurvivalSkills"), PersistentDataType.BOOLEAN)) return false;
        return meta.getCustomModelData() == modelData;
    }

    public static boolean isCustomItem(ItemStack item) {
        if (item == null) return false;
        if (item.getItemMeta() == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasCustomModelData()) return false;
        return meta.getPersistentDataContainer().has(new NamespacedKey(SurvivalSkills.getPlugin(SurvivalSkills.class), "SurvivalSkills"), PersistentDataType.BOOLEAN);
    }

    public static ItemStack getUnlimitedTorch() {
        String name = ColorParser.colorizeString("Unlimited Torch", ColorParser.generateGradient(ColorParser.rgbToHex(255, 0 ,0), ColorParser.rgbToHex(80, 80, 80), 15), true);
        String lore = ChatColor.GRAY + "This torch will never run out!";
        return createCustomItem(Material.TORCH, 1, name, null, lore, null, 1, false, null);
    }

    public static ItemStack getGiantBossItem() {
        String giantName = ColorParser.colorizeString("Giant Head", ColorParser.generateGradient(ColorParser.rgbToHex(0, 255, 0), ColorParser.rgbToHex(80, 80, 80), 10), true);
        String giantLore = ChatColor.GRAY + "I wonder how big his...";
        return createCustomItem(Material.ZOMBIE_HEAD, 1, giantName, null, giantLore, null, 2, false, null);
    }

    public static ItemStack getFishingBossItem() {
        String fishName = ColorParser.colorizeString("Sea King Scale", ColorParser.generateGradient("#00FFFF", "#0000FF", 14), true);
        String fishLore = ChatColor.GRAY + "Your fishing perseverance was noticed by the king";
        return createCustomItem(Material.PRISMARINE_SHARD, 1, fishName, null, fishLore, null, 2, false, null);
    }

    public static ItemStack getBroodMotherBossItem() {
        String spiderName = ColorParser.colorizeString("BroodMother Web", ColorParser.generateGradient("#FFFFFF", "#43000E", 15), true);
        String spiderLore = ChatColor.GRAY + "The Queen recognized your greatness as she fell";
        return createCustomItem(Material.COBWEB, 1, spiderName, null, spiderLore, null, 2, false, null);
    }

    public static ItemStack getElderGuardianBossItem() {
        String fishName = ColorParser.colorizeString("Guardian Eye", ColorParser.generateGradient("#001EFF", "#E900FF", 12), true);
        String fishLore = ChatColor.GRAY + "It seems like it is still watching you";
        return createCustomItem(Material.ENDER_EYE, 1, fishName, null, fishLore, null, 2, false, null);
    }

    public static ItemStack getWardenBossItem() {
        String wardenName = ColorParser.colorizeString("Warden Heart", ColorParser.generateGradient("#323232", "#C776FF", 12), true);
        String wardenLore = ChatColor.GRAY + "You faintly hear the shrieks of ancient times";
        return createCustomItem(Material.ECHO_SHARD, 1, wardenName, null, wardenLore, null, 2, false, null);
    }

    public static ItemStack getVillagerBossItem() {
        String villagerName = ColorParser.colorizeString("Minecraft Essence", ColorParser.generateGradient("#2C1F0C", "#005306", 17), true);
        String villagerLore = ChatColor.GRAY + "The villager gained access to the game code itself";
        return createCustomItem(Material.PLAYER_HEAD, 1, villagerName, null, villagerLore, null, 2, false, null);
    }

    public static ItemStack getEnderDragonBossItem() {
        String dragonName = ColorParser.colorizeString("Dragon Head", ColorParser.generateGradient("#8C00FF", "#180152", 14), true);
        String dragonLore = ChatColor.GRAY + "The ender dragon guarded the void from invaders";
        return createCustomItem(Material.DRAGON_HEAD, 1, dragonName, null, dragonLore, null, 2, false, null);
    }

    public static ItemStack getGiantSummoner() {
        String name = ColorParser.colorizeString("Giant Summoner Egg", ColorParser.generateGradient(ColorParser.rgbToHex(0, 255, 0), ColorParser.rgbToHex(80, 80, 80), "Giant Summoner Egg"), true);
        String lore = ChatColor.GRAY + "Must spawn at night";
        return createCustomItem(Material.ZOMBIE_SPAWN_EGG, 1, name, null, lore, null, 12, false, null);
    }

    public static ItemStack getBroodMotherSummoner() {
        String name = ColorParser.colorizeString("BroodMother Summoner Egg", ColorParser.generateGradient("#FFFFFF", "#43000E", "BroodMother Summoner Egg"), true);
        String lore = ChatColor.GRAY + "Make sure you are prepared";
        return createCustomItem(Material.SPIDER_SPAWN_EGG, 1, name, null, lore, null, 13, false, null);
    }

    public static ItemStack getVillagerSummoner() {
        String name = ColorParser.colorizeString("Villager Summoner Egg", ColorParser.generateGradient("#2C1F0C", "#005306", "Villager Summoner Egg"), true);
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "The true final boss of Minecraft");
        loreList.add(ChatColor.RED + "This boss is very difficult and will require many attempts!");
        loreList.add(ChatColor.RED.toString() + ChatColor.BOLD + "THIS IS A DESTRUCTIVE FIGHT!!!");
        return createCustomItem(Material.VILLAGER_SPAWN_EGG, 1, name, null, null, loreList, 14, false, null);
    }

    public static ItemStack getTrophyItem(Material mat, String name, String lore) {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(ChatColor.GRAY + "A Testament Of Your Progress");
        loreList.add("");
        loreList.add(lore);
        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.KNOCKBACK, 5);
        return createCustomItem(mat, 1, name, null, null, loreList, 999, true, enchants);
    }

    public static ItemStack getMiningHelmet() {
        String name = ChatColor.GRAY.toString() + ChatColor.BOLD + "Mining Helmet";
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Haste II Everywhere");
        loreList.add(ChatColor.GRAY + "Fire Resistance and Speed when below" + ChatColor.AQUA + " Y-64");
        ItemStack helmet = createCustomItem(Material.LEATHER_HELMET, 1, name, null, null, loreList, 3, false, null);
        ItemMeta meta = helmet.getItemMeta();
        if (meta == null) return helmet;
        meta.setUnbreakable(true);

        // Set the color of the helmet to gray
        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
            leatherMeta.setColor(Color.GRAY);
        }

        helmet.setItemMeta(meta);
        return helmet;
    }

    public static ItemStack getMiningChestplate() {
        String name = ChatColor.GRAY.toString() + ChatColor.BOLD + "Mining Chestplate";
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Haste II Everywhere");
        loreList.add(ChatColor.GRAY + "Fire Resistance and Speed when below" + ChatColor.AQUA + " Y-64");
        ItemStack chestplate = createCustomItem(Material.LEATHER_CHESTPLATE, 1, name, null, null, loreList, 3, false, null);
        ItemMeta meta = chestplate.getItemMeta();
        if (meta == null) return chestplate;
        meta.setUnbreakable(true);

        // Set the color of the helmet to gray
        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
            leatherMeta.setColor(Color.GRAY);
        }

        chestplate.setItemMeta(meta);
        return chestplate;
    }

    public static ItemStack getMiningLeggings() {
        String name = ChatColor.GRAY.toString() + ChatColor.BOLD + "Mining Leggings";
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Haste II Everywhere");
        loreList.add(ChatColor.GRAY + "Fire Resistance and Speed when below" + ChatColor.AQUA + " Y-64");
        ItemStack leggings = createCustomItem(Material.LEATHER_LEGGINGS, 1, name, null, null, loreList, 3, false, null);
        ItemMeta meta = leggings.getItemMeta();
        if (meta == null) return leggings;
        meta.setUnbreakable(true);

        // Set the color of the helmet to gray
        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
            leatherMeta.setColor(Color.GRAY);
        }

        leggings.setItemMeta(meta);
        return leggings;
    }

    public static ItemStack getMiningBoots() {
        String name = ChatColor.GRAY.toString() + ChatColor.BOLD + "Mining Boots";
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Haste II Everywhere");
        loreList.add(ChatColor.GRAY + "Fire Resistance and Speed when below" + ChatColor.AQUA + " Y-64");
        ItemStack boots = createCustomItem(Material.LEATHER_BOOTS, 1, name, null, null, loreList, 3, false, null);
        ItemMeta meta = boots.getItemMeta();
        if (meta == null) return boots;
        meta.setUnbreakable(true);

        // Set the color of the helmet to gray
        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
            leatherMeta.setColor(Color.GRAY);
        }

        boots.setItemMeta(meta);
        return boots;
    }

    public static ItemStack getFireResistancePotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        if (meta == null) return potion;
        meta.setBasePotionType(PotionType.FIRE_RESISTANCE);
        potion.setItemMeta(meta);
        return potion;
    }

    public static ItemStack getJumpPowerPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        if (meta == null) return potion;
        meta.setBasePotionType(PotionType.LEAPING);
        potion.setItemMeta(meta);
        return potion;
    }

    public static ItemStack getSpeedPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        if (meta == null) return potion;
        meta.setBasePotionType(PotionType.SWIFTNESS);
        potion.setItemMeta(meta);
        return potion;
    }

    public static ItemStack getJumpingBoots() {
        String name = ColorParser.colorizeString("Jumping Boots", ColorParser.generateGradient("#FFFFFF", "#015210", "Jumping Boots"), true);
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Jump Boost II Everywhere");
        loreList.add(ChatColor.GRAY + "Prevents Fall Damage");
        return createCustomItem(Material.IRON_BOOTS, 1, name, null, null, loreList, 4, false, null);
    }

    public static ItemStack getWandererBoots() {
        String name = ColorParser.colorizeString("Wanderer Boots", ColorParser.generateGradient("#FFF000", "#FFDC6E", "Wanderer Boots"), true);
        String lore = ChatColor.GRAY + "Speed I Everywhere";
        return createCustomItem(Material.CHAINMAIL_BOOTS, 1, name, null, lore, null, 5, false, null);
    }

    public static ItemStack getWandererLeggings() {
        String name = ColorParser.colorizeString("Wanderer Leggings", ColorParser.generateGradient("#FFF000", "#FFDC6E", "Wanderer Leggings"), true);
        String lore = ChatColor.GRAY + "Speed I Everywhere";
        return createCustomItem(Material.CHAINMAIL_LEGGINGS, 1, name, null, lore, null, 5, false, null);
    }

    public static ItemStack getWandererChestplate() {
        String name = ColorParser.colorizeString("Wanderer Chestplate", ColorParser.generateGradient("#FFF000", "#FFDC6E", "Wanderer Chestplate"), true);
        String lore = ChatColor.GRAY + "Speed I Everywhere";
        return createCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, name, null, lore, null, 5, false, null);
    }

    public static ItemStack getWandererHelmet() {
        String name = ColorParser.colorizeString("Wanderer Helmet", ColorParser.generateGradient("#FFF000", "#FFDC6E", "Wanderer Helmet"), true);
        String lore = ChatColor.GRAY + "Speed I Everywhere";
        return createCustomItem(Material.CHAINMAIL_HELMET, 1, name, null, lore, null, 5, false, null);
    }

    public static ItemStack getCaveFinder() {
        String name = ColorParser.colorizeString("Cave Finder", ColorParser.generateGradient("#FFFFFF", "#000000", "Cave Finder"), true);
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Right Click to find the nearest cave");
        loreList.add(ChatColor.GRAY + "Tells you if it is a confirmed cave or a potential dark spot");
        return createCustomItem(Material.COMPASS, 1, name, null, null, loreList, 6, false, null);
    }

    public static ItemStack getTravellerBoots() {
        String name = ColorParser.colorizeString("Traveller Boots", ColorParser.generateGradient("#FF8800", "#FFDC6E", "Traveller Boots"), true);
        String lore = ChatColor.GRAY + "Speed II Everywhere";
        return createCustomItem(Material.DIAMOND_BOOTS, 1, name, null, lore, null, 7, false, null);
    }

    public static ItemStack getTravellerLeggings() {
        String name = ColorParser.colorizeString("Traveller Leggings", ColorParser.generateGradient("#FF8800", "#FFDC6E", "Traveller Leggings"), true);
        String lore = ChatColor.GRAY + "Speed II Everywhere";
        return createCustomItem(Material.DIAMOND_LEGGINGS, 1, name, null, lore, null, 7, false, null);
    }

    public static ItemStack getTravellerChestplate() {
        String name = ColorParser.colorizeString("Traveller Chestplate", ColorParser.generateGradient("#FF8800", "#FFDC6E", "Traveller Chestplate"), true);
        String lore = ChatColor.GRAY + "Speed II Everywhere";
        return createCustomItem(Material.DIAMOND_CHESTPLATE, 1, name, null, lore, null, 7, false, null);
    }

    public static ItemStack getTravellerHelmet() {
        String name = ColorParser.colorizeString("Traveller Helmet", ColorParser.generateGradient("#FF8800", "#FFDC6E", "Traveller Helmet"), true);
        String lore = ChatColor.GRAY + "Speed II Everywhere";
        return createCustomItem(Material.DIAMOND_HELMET, 1, name, null, lore, null, 7, false, null);
    }

    public static ItemStack getAdventurerBoots() {
        String name = ColorParser.colorizeString("Adventurer Boots", ColorParser.generateGradient("#FF0000", "#FFDC6E", "Adventurer Boots"), true);
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Speed III Everywhere");
        loreList.add(ChatColor.GRAY + "Prevents Fall Damage");
        ItemStack item = createCustomItem(Material.NETHERITE_BOOTS, 1, name, null, null, loreList, 8, false, null);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getAdventurerLeggings() {
        String name = ColorParser.colorizeString("Adventurer Leggings", ColorParser.generateGradient("#FF0000", "#FFDC6E", "Adventurer Leggings"), true);
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Speed III Everywhere");
        loreList.add(ChatColor.GRAY + "Prevents Fall Damage");
        ItemStack item = createCustomItem(Material.NETHERITE_LEGGINGS, 1, name, null, null, loreList, 8, false, null);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getAdventurerChestplate() {
        String name = ColorParser.colorizeString("Adventurer Chestplate", ColorParser.generateGradient("#FF0000", "#FFDC6E", "Adventurer Chestplate"), true);
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Speed III Everywhere");
        loreList.add(ChatColor.GRAY + "Prevents Fall Damage");
        ItemStack item = createCustomItem(Material.NETHERITE_CHESTPLATE, 1, name, null, null, loreList, 8, false, null);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getAdventurerHelmet() {
        String name = ColorParser.colorizeString("Adventurer Helmet", ColorParser.generateGradient("#FF0000", "#FFDC6E", "Adventurer Helmet"), true);
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Speed III Everywhere");
        loreList.add(ChatColor.GRAY + "Prevents Fall Damage");
        ItemStack item = createCustomItem(Material.NETHERITE_HELMET, 1, name, null, null, loreList, 8, false, null);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getWateringCan() {
        String name = ColorParser.colorizeString("Watering Can", ColorParser.generateGradient("#00FFFF", "#000000", "Watering Can"), true);
        String lore = ChatColor.GRAY + "Right Click to water crops in a 5x5 area";
        return createCustomItem(Material.PRISMARINE_SHARD, 1, name, null, lore, null, 9, false, null);
    }

    public static ItemStack getUnlimitedBoneMeal() {
        String name = ColorParser.colorizeString("Unlimited Bonemeal", ColorParser.generateGradient("#000000", "#515151", "Unlimited Bonemeal"), true);
        String lore = ChatColor.GRAY + "Infinite crop growth";
        return createCustomItem(Material.BONE_MEAL, 1, name, null, lore, null, 10, false, null);
    }

    public static ItemStack getHarvester() {
        String name = ColorParser.colorizeString("Harvester", ColorParser.generateGradient("#00FF00", "#000000", "Harvester"), true);
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Breaks and replants crops for you!");
        loreList.add(ChatColor.GRAY + "Cooldown of 3 seconds");
        ItemStack item = createCustomItem(Material.NETHERITE_HOE, 1, name, null, null, loreList, 11, false, null);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getSortOfStonePick() {
        String name = ChatColor.GRAY.toString() + ChatColor.BOLD + "Sort Of Stone Pickaxe";
        String lore = ChatColor.GRAY + "Sure";
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.EFFICIENCY, 2);
        enchants.put(Enchantment.UNBREAKING, 5);
        return createCustomItem(Material.STONE_PICKAXE, 1, name, null, lore, null, 0, false, enchants);
    }

    public static ItemStack getSortWand() {
        String name = ChatColor.BLUE.toString() + ChatColor.BOLD + "Sort Wand";
        String lore = ChatColor.GRAY + "Left click a chest to sort its inventory";
        return createCustomItem(Material.BLAZE_ROD, 1, name, null, lore, null, 16, false, null);
    }

    public static ItemStack getUnlimitedTropicalFishBucket() {
        String name = ColorParser.colorizeString("Unlimited Tropical Fish Bucket",
                ColorParser.generateGradient("#084CFB", "#ADF3FD", "Unlimited Tropical Fish Bucket"), true);
        String lore = ChatColor.GRAY + "Infinite tropical fish";
        return createCustomItem(Material.TROPICAL_FISH_BUCKET, 1, name, null, lore, null, 17, false, null);
    }

    public static ItemStack getFireworkCannon() {
        List<List<String>> colors = new ArrayList<>();
        colors.add(ColorParser.generateGradient("#FF0000", "#00FF00", 5));
        colors.add(ColorParser.generateGradient("#00FF00", "#0000FF", 5));
        colors.add(ColorParser.generateGradient("#0000FF", "#FF00FF", 5));
        String name = ColorParser.colorizeString("Firework Cannon", ColorParser.gradientConnector(colors), true);
        String lore = ChatColor.GRAY + "Shoots a firework in the direction you are looking";
        return createCustomItem(Material.CAMPFIRE, 1, name, null, lore, null, 18, false, null);
    }
}
