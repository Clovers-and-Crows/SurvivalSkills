package sir_draco.survivalskills.Commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import sir_draco.survivalskills.ItemStackGenerator;
import sir_draco.survivalskills.Rewards.Reward;
import sir_draco.survivalskills.Rewards.RewardItemInfo;
import sir_draco.survivalskills.Rewards.RewardNotifications;
import sir_draco.survivalskills.Skill;
import sir_draco.survivalskills.SurvivalSkills;

import java.util.*;

public class SkillStatsCommand implements CommandExecutor {

    private final SurvivalSkills plugin;
    private final ArrayList<Inventory> recipeInventories = new ArrayList<>();
    private final ArrayList<RewardItemInfo> recipeLevelInformation = new ArrayList<>();

    public SkillStatsCommand(SurvivalSkills plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("skills");
        if (command == null) return;
        command.setExecutor(this);
        getItemLevelInformation();
        createRecipeInventories();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;

        if (strings.length == 0) {
            for (Skill skill : plugin.getPlayerSkills().get(p.getUniqueId())) skill.printStats(p, true);
            return true;
        }

        if (strings[0].equalsIgnoreCase("tree")) {
            if (strings.length == 1) {
                p.sendRawMessage(ChatColor.RED + "Please specify a skill tree to view.");
                p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                return true;
            }

            if (strings[1].equalsIgnoreCase("mining")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArrayList<Inventory> inventories = createSkillTree(p, Skill.MINING);
                        if (inventories == null || inventories.isEmpty()) return;
                        plugin.getPlayerListener().getCustomInventories().put(p, inventories);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.openInventory(inventories.get(0));
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }
            else if (strings[1].equalsIgnoreCase("farming")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArrayList<Inventory> inventories = createSkillTree(p, Skill.FARMING);
                        if (inventories == null || inventories.isEmpty()) return;
                        plugin.getPlayerListener().getCustomInventories().put(p, inventories);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.openInventory(inventories.get(0));
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }
            else if (strings[1].equalsIgnoreCase("fighting")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArrayList<Inventory> inventories = createSkillTree(p, Skill.FIGHTING);
                        if (inventories == null || inventories.isEmpty()) return;
                        plugin.getPlayerListener().getCustomInventories().put(p, inventories);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.openInventory(inventories.get(0));
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }
            else if (strings[1].equalsIgnoreCase("crafting")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArrayList<Inventory> inventories = createSkillTree(p, Skill.CRAFTING);
                        if (inventories == null || inventories.isEmpty()) return;
                        plugin.getPlayerListener().getCustomInventories().put(p, inventories);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.openInventory(inventories.get(0));
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }
            else if (strings[1].equalsIgnoreCase("main")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArrayList<Inventory> inventories = createSkillTree(p, Skill.MAIN);
                        if (inventories == null || inventories.isEmpty()) return;
                        plugin.getPlayerListener().getCustomInventories().put(p, inventories);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.openInventory(inventories.get(0));
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }
            else if (strings[1].equalsIgnoreCase("building")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArrayList<Inventory> inventories = createSkillTree(p, Skill.BUILDING);
                        if (inventories == null || inventories.isEmpty()) return;
                        plugin.getPlayerListener().getCustomInventories().put(p, inventories);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.openInventory(inventories.get(0));
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }
            else if (strings[1].equalsIgnoreCase("fishing")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArrayList<Inventory> inventories = createSkillTree(p, Skill.FISHING);
                        if (inventories == null || inventories.isEmpty()) return;
                        plugin.getPlayerListener().getCustomInventories().put(p, inventories);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.openInventory(inventories.get(0));
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }
            else if (strings[1].equalsIgnoreCase("exploring")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArrayList<Inventory> inventories = createSkillTree(p, Skill.EXPLORING);
                        if (inventories == null || inventories.isEmpty()) return;
                        plugin.getPlayerListener().getCustomInventories().put(p, inventories);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.openInventory(inventories.get(0));
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }
            else if (strings[1].equalsIgnoreCase("deaths")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Inventory inventory = Bukkit.createInventory(null, 9);
                        int deaths = plugin.getLeaderboardScore(p, "Deaths");
                        createDeathTree(inventory, deaths);
                        ArrayList<Inventory> inventories = new ArrayList<>();
                        inventories.add(inventory);
                        if (inventory.isEmpty()) return;
                        plugin.getPlayerListener().getCustomInventories().put(p, inventories);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.openInventory(inventory);
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }

            p.sendRawMessage(ChatColor.RED + "Invalid skill tree.");
            p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            return true;
        }

        if (strings[0].equalsIgnoreCase("recipes")) {
            plugin.getPlayerListener().getCustomInventories().put(p, recipeInventories);
            p.openInventory(recipeInventories.get(0));
        }

        if (strings[0].equalsIgnoreCase("commands")) {
            p.sendRawMessage(ChatColor.AQUA + "Commands:");
            p.sendRawMessage(ChatColor.GRAY + "/skills - View your skill stats");
            p.sendRawMessage(ChatColor.GRAY + "/skills tree <skill> - View the skill tree for a specific skill");
            p.sendRawMessage(ChatColor.GRAY + "/skills recipes - View all skill recipes");
            p.sendRawMessage(ChatColor.GRAY + "/skills trophies - Explains how the trophy system works");
            p.sendRawMessage(ChatColor.GRAY + "/skills player <name> - Gets the skill stats of another player");
            p.sendRawMessage(ChatColor.GRAY + "/togglescorboard - Enable or disable the scoreboard");
            p.sendRawMessage(ChatColor.GRAY + "/spelunker - tells you where ores are, unlocked through mining skill");
            p.sendRawMessage(ChatColor.GRAY + "/veinminer - unlocked through mining skill");
            p.sendRawMessage(ChatColor.GRAY + "/ssnv - night vision, unlocked through mining skill");
            p.sendRawMessage(ChatColor.GRAY + "/peacefulminer - mobs won't spawn while mining, unlocked through mining skill");
            p.sendRawMessage(ChatColor.GRAY + "/autoeat - automatically eat food from your inventory, unlocked through farming skill");
            p.sendRawMessage(ChatColor.GRAY + "/sseat - feeds you without needing food, unlocked through farming skill");
            p.sendRawMessage(ChatColor.GRAY + "/flight - unlocked through building skill");
            p.sendRawMessage(ChatColor.GRAY + "/mobscanner - shows nearby mobs, unlocked through fighting skill");
            p.sendRawMessage(ChatColor.GRAY + "/waterbreathing - unlocked through fishing skill");
            p.sendRawMessage(ChatColor.GRAY + "/autotrash - unlocked through fishing skill");
            p.sendRawMessage(ChatColor.GRAY + "/deathlocation - shows your death location, unlocked through main skill");
            p.sendRawMessage(ChatColor.GRAY + "/toggletrail <trail> - enables or disables a particle trail, unlocked through main skill");
            p.sendRawMessage(ChatColor.GRAY + "/deathreturn - returns you to your death location, unlocked through death skill");
        }

        if (strings[0].equalsIgnoreCase("trophies")) {
            p.sendRawMessage(ChatColor.AQUA + "Trophy Information:");
            p.sendRawMessage(ChatColor.GRAY + "Trophies are crafted using items gathered throughout the game");
            p.sendRawMessage(ChatColor.GRAY + "Trophies increase the level cap of all skills by 10 (level cap starts at 10)");
            p.sendRawMessage(ChatColor.GRAY + "By crafting all trophies you can reach level 100 in all skills");
            p.sendRawMessage(ChatColor.GRAY + "Trophies have animations when placed but once crafted you no longer need the physical trophy");
            p.sendRawMessage(ChatColor.GRAY + "The easiest trophies to craft are the cave, farm, and forest trophies");
            p.sendRawMessage(ChatColor.RED + "Making the same trophy multiple times does not increase the cap");
            p.sendRawMessage(ChatColor.GOLD + "See trophy recipes by using " + ChatColor.AQUA + "/skills recipes");
            p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            return true;
        }

        if (strings[0].equalsIgnoreCase("player")) {
            if (strings.length < 2) {
                p.sendRawMessage(ChatColor.RED + "Correct Usage: /skills player <name>");
                p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                return true;
            }

            Player play = null;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getName().equals(strings[1])) continue;
                play = player;
            }
            if (play == null) {
                p.sendRawMessage(ChatColor.RED + "Player: " + ChatColor.YELLOW + strings[1] + ChatColor.RED + " not found");
                p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                return true;
            }

            for (Skill skill : plugin.getPlayerSkills().get(play.getUniqueId())) skill.printStats(p, false);
            return true;
        }

        if (strings[0].equalsIgnoreCase("leaderboard")) {
            if (plugin.getLeaderboardTracker().isEmpty()) {
                p.sendRawMessage(ChatColor.RED + "Leaderboard is empty, level up a skill first!");
                p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                return true;
            }

            if (strings.length == 1) {
                ArrayList<String> leaderboard = plugin.getTopTen();
                for (String line : leaderboard) p.sendRawMessage(line);
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                return true;
            }

            double size = Math.ceil((double) plugin.getLeaderboardTracker().size() / 10);
            int maxPage = Math.max(1, (int) Math.ceil(size));
            if (strings[1].equalsIgnoreCase("all")) {
                if (strings.length != 3) {
                    printLeaderboard(p, "All", 1, maxPage);
                    return true;
                }

                try {
                    int page = Integer.parseInt(strings[2]);
                    if (page < 1 || page > maxPage) {
                        p.sendRawMessage(ChatColor.RED + "Invalid page number. Max page number is: " + ChatColor.AQUA + maxPage);
                        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                        return true;
                    }
                    printLeaderboard(p, "All", page, maxPage);
                } catch (NumberFormatException e) {
                    p.sendRawMessage(ChatColor.RED + "Invalid page number.");
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            else if (strings[1].equalsIgnoreCase("building")) {
                if (strings.length != 3) {
                    printLeaderboard(p, "Building", 1, maxPage);
                    return true;
                }

                try {
                    int page = Integer.parseInt(strings[2]);
                    if (page < 1 || page > maxPage) {
                        p.sendRawMessage(ChatColor.RED + "Invalid page number. Max page number is: " + ChatColor.AQUA + maxPage);
                        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                        return true;
                    }
                    printLeaderboard(p, "Building", page, maxPage);
                } catch (NumberFormatException e) {
                    p.sendRawMessage(ChatColor.RED + "Invalid page number.");
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            else if (strings[1].equalsIgnoreCase("crafting")) {
                if (strings.length != 3) {
                    printLeaderboard(p, "Crafting", 1, maxPage);
                    return true;
                }

                try {
                    int page = Integer.parseInt(strings[2]);
                    if (page < 1 || page > maxPage) {
                        p.sendRawMessage(ChatColor.RED + "Invalid page number. Max page number is: " + ChatColor.AQUA + maxPage);
                        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                        return true;
                    }
                    printLeaderboard(p, "Crafting", page, maxPage);
                } catch (NumberFormatException e) {
                    p.sendRawMessage(ChatColor.RED + "Invalid page number.");
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            else if (strings[1].equalsIgnoreCase("exploring")) {
                if (strings.length != 3) {
                    printLeaderboard(p, "Exploring", 1, maxPage);
                    return true;
                }

                try {
                    int page = Integer.parseInt(strings[2]);
                    if (page < 1 || page > maxPage) {
                        p.sendRawMessage(ChatColor.RED + "Invalid page number. Max page number is: " + ChatColor.AQUA + maxPage);
                        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                        return true;
                    }
                    printLeaderboard(p, "Exploring", page, maxPage);
                } catch (NumberFormatException e) {
                    p.sendRawMessage(ChatColor.RED + "Invalid page number.");
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            else if (strings[1].equalsIgnoreCase("farming")) {
                if (strings.length != 3) {
                    printLeaderboard(p, "Farming", 1, maxPage);
                    return true;
                }

                try {
                    int page = Integer.parseInt(strings[2]);
                    if (page < 1 || page > maxPage) {
                        p.sendRawMessage(ChatColor.RED + "Invalid page number. Max page number is: " + ChatColor.AQUA + maxPage);
                        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    }
                    printLeaderboard(p, "Farming", page, maxPage);
                }
                catch (NumberFormatException e) {
                    p.sendRawMessage(ChatColor.RED + "Invalid page number.");
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            else if (strings[1].equalsIgnoreCase("fighting")) {
                if (strings.length != 3) {
                    printLeaderboard(p, "Fighting", 1, maxPage);
                    return true;
                }

                try {
                    int page = Integer.parseInt(strings[2]);
                    if (page < 1 || page > maxPage) {
                        p.sendRawMessage(ChatColor.RED + "Invalid page number. Max page number is: " + ChatColor.AQUA + maxPage);
                        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    }
                    printLeaderboard(p, "Fighting", page, maxPage);
                }
                catch (NumberFormatException e) {
                    p.sendRawMessage(ChatColor.RED + "Invalid page number.");
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            else if (strings[1].equalsIgnoreCase("fishing")) {
                if (strings.length != 3) {
                    printLeaderboard(p, "Fishing", 1, maxPage);
                    return true;
                }

                try {
                    int page = Integer.parseInt(strings[2]);
                    if (page < 1 || page > maxPage) {
                        p.sendRawMessage(ChatColor.RED + "Invalid page number. Max page number is: " + ChatColor.AQUA + maxPage);
                        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    }
                    printLeaderboard(p, "Fishing", page, maxPage);
                }
                catch (NumberFormatException e) {
                    p.sendRawMessage(ChatColor.RED + "Invalid page number.");
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            else if (strings[1].equalsIgnoreCase("mining")) {
                if (strings.length != 3) {
                    printLeaderboard(p, "Mining", 1, maxPage);
                    return true;
                }

                try {
                    int page = Integer.parseInt(strings[2]);
                    if (page < 1 || page > maxPage) {
                        p.sendRawMessage(ChatColor.RED + "Invalid page number. Max page number is: " + ChatColor.AQUA + maxPage);
                        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    }
                    printLeaderboard(p, "Mining", page, maxPage);
                }
                catch (NumberFormatException e) {
                    p.sendRawMessage(ChatColor.RED + "Invalid page number.");
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            else if (strings[1].equalsIgnoreCase("main")) {
                if (strings.length != 3) {
                    printLeaderboard(p, "Main", 1, maxPage);
                    return true;
                }

                try {
                    int page = Integer.parseInt(strings[2]);
                    if (page < 1 || page > maxPage) {
                        p.sendRawMessage(ChatColor.RED + "Invalid page number. Max page number is: " + ChatColor.AQUA + maxPage);
                        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    }
                    printLeaderboard(p, "Main", page, maxPage);
                }
                catch (NumberFormatException e) {
                    p.sendRawMessage(ChatColor.RED + "Invalid page number.");
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            else if (strings[1].equalsIgnoreCase("deaths")) {
                if (strings.length != 3) {
                    printLeaderboard(p, "Deaths", 1, maxPage);
                    return true;
                }

                try {
                    int page = Integer.parseInt(strings[2]);
                    if (page < 1 || page > maxPage) {
                        p.sendRawMessage(ChatColor.RED + "Invalid page number. Max page number is: " + ChatColor.AQUA + maxPage);
                        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    }
                    printLeaderboard(p, "Deaths", page, maxPage);
                }
                catch (NumberFormatException e) {
                    p.sendRawMessage(ChatColor.RED + "Invalid page number.");
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            return true;
        }
        return true;
    }

    private static ItemStack getItemStack(int playerLevel, int i) {
        ItemStack item;
        if (playerLevel >= i) {
            item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return item;
            meta.setDisplayName("(" + ChatColor.GREEN + i + ChatColor.WHITE + ")");
            item.setItemMeta(meta);
        }
        else {
            item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return item;
            meta.setDisplayName("(" + ChatColor.RED + i + ChatColor.WHITE + ")");
            item.setItemMeta(meta);
        }
        return item;
    }

    public void createRecipeInventories() {
        int recipeCounter = 1;
        int totalPages = plugin.getRecipeKeys().size() / 2 + 1;
        for (;recipeCounter <= plugin.getRecipeKeys().size(); recipeCounter++) {
            Inventory inv;
            if (recipeCounter % 2 == 1) {
                int pageNumber = (recipeCounter + 1) / 2;
                inv = Bukkit.createInventory(null, 36, "Skill Recipes " + pageNumber + "/" + totalPages);
                addSSRecipe(recipeCounter, inv);
                recipeInventories.add(inv);
                if (recipeCounter == plugin.getRecipeKeys().size()) addBarriers(inv);
            }
            else {
                inv = recipeInventories.get(recipeInventories.size() - 1);
                addSSRecipe(recipeCounter, inv);
                addBarriers(inv);
            }
        }

        if (recipeCounter % 2 == 1) addBarriers(recipeInventories.get(recipeInventories.size() - 1));
    }

    public void addSSRecipe(int recipeCounter, Inventory inv) {
        NamespacedKey key = plugin.getRecipeKeys().get(recipeCounter - 1);
        if (key == null) return;
        ArrayList<Integer> slots = getRecipeSlots(recipeCounter);
        Recipe recipe = Bukkit.getRecipe(key);
        if (recipe == null) return;
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
            String[] shape = shapedRecipe.getShape();
            Map<Character, ItemStack> ingredients = shapedRecipe.getIngredientMap();
            Map<Character, RecipeChoice> recipeChoices = shapedRecipe.getChoiceMap();
            for (int i = 0; i < shape.length * 3; i++) {
                int slot = i % 3;
                String layer;
                if (i <= 2) layer = shape[0];
                else if (i <= 5) layer = shape[1];
                else layer = shape[2];
                if (slot >= layer.length()) continue;
                char c = layer.charAt(slot);

                if (c == ' ' || c == 'D') continue;
                ItemStack ingredient = ingredients.get(c);
                if (ingredient != null && ingredient.getType().equals(Material.PRISMARINE_SHARD)
                        && ingredient.getItemMeta() != null && ingredient.getItemMeta().hasCustomModelData()) {
                    ArrayList<String> lore = (ArrayList<String>) ingredient.getItemMeta().getLore();
                    if (lore != null) {
                        lore.add(ChatColor.GRAY + "Unlocked at fighting level: " + ChatColor.AQUA +
                                plugin.getDefaultPlayerRewards().getReward("Fighting", "FishingKing").getLevel());
                        ItemMeta meta = ingredient.getItemMeta();
                        meta.setLore(lore);
                        ingredient.setItemMeta(meta);
                    }
                }
                if (ingredients.containsKey(c)) inv.setItem(slots.get(i), ingredients.get(c));
                else if (recipeChoices.containsKey(c)) inv.setItem(slots.get(i), recipeChoices.get(c).getItemStack());
            }
            ItemStack result = getResult(shapedRecipe.getResult());
            inv.setItem(slots.get(9), result);
        }
        else if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
            List<ItemStack> ingredients = shapelessRecipe.getIngredientList();
            int slot = 0;
            if (!ingredients.isEmpty()) {
                for (ItemStack ingredient : ingredients) {
                    inv.setItem(slots.get(slot), ingredient);
                    slot++;
                }
            }

            ItemStack result = getResult(shapelessRecipe.getResult());
            inv.setItem(slots.get(9), result);
        }
    }

    public ArrayList<Integer> getRecipeSlots(int recipeCounter) {
        ArrayList<Integer> slots = new ArrayList<>();
        if (recipeCounter % 2 == 1) {
            slots.add(0);
            slots.add(1);
            slots.add(2);
            slots.add(9);
            slots.add(10);
            slots.add(11);
            slots.add(18);
            slots.add(19);
            slots.add(20);
            slots.add(12);
        }
        else {
            slots.add(5);
            slots.add(6);
            slots.add(7);
            slots.add(14);
            slots.add(15);
            slots.add(16);
            slots.add(23);
            slots.add(24);
            slots.add(25);
            slots.add(17);
        }
        return slots;
    }

    public ArrayList<Inventory> createSkillTree(Player p, String skill) {
        ArrayList<Inventory> inventories = new ArrayList<>();
        int playerLevel = plugin.getSkill(p.getUniqueId(), skill).getLevel();
        HashMap<String, ArrayList<Reward>> allSkills = plugin.getDefaultPlayerRewards().getRewardList();
        ArrayList<Reward> rewards = null;
        switch (skill) {
            case Skill.MINING:
                rewards = allSkills.get("Mining");
                break;
            case Skill.FARMING:
                rewards = allSkills.get("Farming");
                break;
            case Skill.FIGHTING:
                rewards = allSkills.get("Fighting");
                break;
            case Skill.CRAFTING:
                rewards = allSkills.get("Crafting");
                break;
            case Skill.MAIN:
                rewards = allSkills.get("Main");
                break;
            case Skill.BUILDING:
                rewards = allSkills.get("Building");
                break;
            case Skill.FISHING:
                rewards = allSkills.get("Fishing");
                break;
            case Skill.EXPLORING:
                rewards = allSkills.get("Exploring");
                break;
        }
        if (rewards == null) return null;

        int i = 1;
        for (int a = 0; a < 5; a++) {
            i = createSkillInventory(i, playerLevel, rewards, inventories);
            if (i >= 100) break;
        }
        return inventories;
    }

    public int createSkillInventory(int start, int playerLevel, ArrayList<Reward> rewards, ArrayList<Inventory> inventories) {
        Inventory inv = Bukkit.createInventory(null, 54, "Skill Tree");
        int i = start;
        int slot = 0;
        for (;i <= start + 29; i++) {
            boolean foundReward = false;
            for (Reward reward : rewards) {
                if (reward.getLevel() != i) continue;
                if (!reward.isEnabled()) continue;
                ItemStack item;
                if (playerLevel >= i) {
                    item = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) continue;
                    meta.setDisplayName("(" + ChatColor.GREEN + i + ChatColor.WHITE + ") " + ChatColor.GREEN + addSpaces(reward.getName()));
                    meta.setLore(RewardNotifications.getLore(RewardNotifications.getRewardDescription(reward.getSkillType(), reward.getName())));
                    item.setItemMeta(meta);
                }
                else {
                    item = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) continue;
                    meta.setDisplayName("(" + ChatColor.RED + i + ChatColor.WHITE + ") " + ChatColor.RED + addSpaces(reward.getName()));
                    meta.setLore(RewardNotifications.getLore(RewardNotifications.getRewardDescription(reward.getSkillType(), reward.getName())));
                    item.setItemMeta(meta);
                }
                inv.setItem(slot, item);
                foundReward = true;
                break;
            }
            
            if (!foundReward) {
                ItemStack item = getItemStack(playerLevel, i);
                inv.setItem(slot, item);
            }

            if (slot < 8) slot++; // right
            else if (slot == 8) slot = 17; // down
            else if (slot == 17) slot = 26; // down
            else if (slot > 18 && slot <= 26) slot--; // left
            else if (slot == 18) slot = 27; // down
            else if (slot == 27) slot = 36; // down
            else if (slot >= 36) slot++; // right

            if (i >= 100) break;
        }

        addBarriers(inv);
        inventories.add(inv);
        return i - 1;
    }

    private void addBarriers(Inventory inv) {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta meta = back.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(ChatColor.RED + "Back");
        back.setItemMeta(meta);

        ItemStack front = new ItemStack(Material.ARROW);
        meta = front.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(ChatColor.BLUE + "Next");
        meta.setCustomModelData(1);
        front.setItemMeta(meta);

        if (inv.getSize() >= 53) {
            inv.setItem(45, item);
            inv.setItem(46, item);
            inv.setItem(47, item);
            inv.setItem(48, back);
            inv.setItem(49, item);
            inv.setItem(50, front);
            inv.setItem(51, item);
            inv.setItem(52, item);
            inv.setItem(53, item);
        }
        else {
            inv.setItem(27, item);
            inv.setItem(28, item);
            inv.setItem(29, item);
            inv.setItem(30, back);
            inv.setItem(31, item);
            inv.setItem(32, front);
            inv.setItem(33, item);
            inv.setItem(34, item);
            inv.setItem(35, item);
        }
    }

    public String addSpaces(String input) {
        StringBuilder result = new StringBuilder();
        boolean firstRoman = true;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (i == 0) {
                result.append(c);
                continue;
            }
            if (Character.isUpperCase(c) && i+1 < input.length() && Character.isLowerCase(input.charAt(i+1))) {
                result.append(" ");
                result.append(c);
                continue;
            }
            if (Character.isUpperCase(c) && c != 'I' && c != 'V' && c!='X') result.append(" ");
            if (c == 'I' || c == 'V' || c == 'X') {
                if (firstRoman) {
                    firstRoman = false;
                    result.append(" ");
                }
            }
            result.append(c);
        }
        return result.toString();
    }

    public void createDeathTree(Inventory inv, int deaths) {
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                ItemStack item;
                if (deaths >= 10) {
                    item = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.GREEN + "10 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.GREEN + " Temporary Speed Boost After Death");
                    item.setItemMeta(meta);
                }
                else {
                    item = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.RED + "10 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.RED + " Temporary Speed Boost After Death");
                    item.setItemMeta(meta);
                }
                inv.setItem(i, item);
            }
            else if (i == 1) {
                ItemStack item;
                if (deaths >= 20) {
                    item = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.GREEN + "20 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.GREEN + " Temporary Strength Boost After Death");
                    item.setItemMeta(meta);
                }
                else {
                    item = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.RED + "20 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.RED + " Temporary Strength Boost After Death");
                    item.setItemMeta(meta);
                }
                inv.setItem(i, item);
            }
            else if (i == 2) {
                ItemStack item;
                if (deaths >= 30) {
                    item = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.GREEN + "30 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.GREEN + " Temporary Regeneration Boost After Death");
                    item.setItemMeta(meta);
                }
                else {
                    item = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.RED + "30 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.RED + " Temporary Regeneration Boost After Death");
                    item.setItemMeta(meta);
                }
                inv.setItem(i, item);
            }
            else if (i == 3) {
                ItemStack item;
                if (deaths >= 40) {
                    item = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.GREEN + "40 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.GREEN + " Fire Resistance");
                    item.setItemMeta(meta);
                }
                else {
                    item = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.RED + "40 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.RED + " Fire Resistance");
                    item.setItemMeta(meta);
                }
                inv.setItem(i, item);
            }
            else if (i == 4) {
                ItemStack item;
                if (deaths >= 50) {
                    item = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.GREEN + "50 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.GREEN + " 10% Less Damage");
                    item.setItemMeta(meta);
                }
                else {
                    item = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.RED + "50 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.RED + " 10% Less Damage");
                    item.setItemMeta(meta);
                }
                inv.setItem(i, item);
            }
            else {
                ItemStack item;
                if (deaths >= 75) {
                    item = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.GREEN + "75 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.GREEN + " Teleport To Death Location");
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Use the command " + ChatColor.AQUA + "/deathreturn " + ChatColor.GRAY + "to teleport to your death location");
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                else {
                    item = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;
                    meta.setDisplayName(ChatColor.WHITE + "(" + ChatColor.RED + "75 Deaths" + ChatColor.WHITE + ")"
                            + ChatColor.RED + " Teleport To Death Location");
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Use the command " + ChatColor.AQUA + "/deathreturn " + ChatColor.GRAY + "to teleport to your death location");
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                inv.setItem(i, item);
            }
        }
    }

    public RewardItemInfo getRewardItemInfo(ItemStack item, String skillName, String rewardName) {
        return new RewardItemInfo(item, skillName, rewardName, plugin.getRewardLevel(skillName, rewardName));
    }

    public void getItemLevelInformation() {
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getUnlimitedTorch(), "Mining", "UnlimitedTorch"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getMiningHelmet(), "Mining", "MiningArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getMiningChestplate(), "Mining", "MiningArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getMiningLeggings(), "Mining", "MiningArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getMiningBoots(), "Mining", "MiningArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getJumpingBoots(), "Exploring", "JumpingBoots"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getWandererHelmet(), "Exploring", "WandererArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getWandererChestplate(), "Exploring", "WandererArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getWandererLeggings(), "Exploring", "WandererArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getWandererBoots(), "Exploring", "WandererArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getTravellerHelmet(), "Exploring", "TravellerArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getTravellerChestplate(), "Exploring", "TravellerArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getTravellerLeggings(), "Exploring", "TravellerArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getTravellerBoots(), "Exploring", "TravellerArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getAdventurerHelmet(), "Exploring", "AdventurerArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getAdventurerChestplate(), "Exploring", "AdventurerArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getAdventurerLeggings(), "Exploring", "AdventurerArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getAdventurerBoots(), "Exploring", "AdventurerArmor"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getCaveFinder(), "Exploring", "CaveFinder"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getWateringCan(), "Farming", "WateringCan"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getUnlimitedBoneMeal(), "Farming", "UnlimitedBonemeal"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getHarvester(), "Farming", "Harvester"));
        recipeLevelInformation.add(getRewardItemInfo(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE), "Crafting", "EnchantedGapple"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getFireworkCannon(), "Main", "FireworkCannon"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getSortWand(), "Building", "AutoSortWand"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getGiantSummoner(), "Fighting", "GiantSummon"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getBroodMotherSummoner(), "Fighting", "BroodMotherSummon"));
        recipeLevelInformation.add(getRewardItemInfo(ItemStackGenerator.getVillagerSummoner(), "Fighting", "TheExiledOneSummon"));
    }

    public ItemStack getResult(ItemStack item) {
        for (RewardItemInfo info : recipeLevelInformation) {
            if (!info.isItem(item)) continue;
            ItemStack result = new ItemStack(info.getItem());
            ItemMeta meta = result.getItemMeta();
            if (meta == null) return item;
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Unlocked when " + ChatColor.AQUA + info.getSkillName() + ChatColor.GRAY
                        + " reaches level " + ChatColor.AQUA + info.getLevel());
                meta.setLore(lore);
                result.setItemMeta(meta);
                return result;
            }
            lore.add("");
            lore.add(ChatColor.GRAY + "Unlocked when " + ChatColor.AQUA + info.getSkillName() + ChatColor.GRAY
                    + " reaches level " + ChatColor.AQUA + info.getLevel());
            meta.setLore(lore);
            result.setItemMeta(meta);
            return result;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        List<String> lore = meta.getLore();
        if (lore == null) return item;
        lore.add("");
        lore.add(ChatColor.GRAY + "No requirements to unlock recipe");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void printLeaderboard(Player p, String skillName, int page, int maxPage) {
        ArrayList<String> leaderboard = plugin.sortLeaderboard(skillName);

        p.sendRawMessage(ChatColor.YELLOW + "Page: " + ChatColor.AQUA + page + ChatColor.YELLOW + "/" + ChatColor.AQUA + maxPage);
        plugin.printRank(p, skillName);
        for (int i = (page * 10) - 10; i <= (page * 10) - 1; i++) {
            if (i >= leaderboard.size()) break;
            p.sendRawMessage(leaderboard.get(i));
        }
        p.sendRawMessage(ChatColor.YELLOW + "Type " + ChatColor.AQUA + "/skills leaderboard " + skillName + " <page>" + ChatColor.YELLOW + " to view more pages");
        p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }
}
