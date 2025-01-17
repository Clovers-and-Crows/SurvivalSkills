package sir_draco.survivalskills.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sir_draco.survivalskills.ItemStackGenerator;
import sir_draco.survivalskills.SurvivalSkills;

public class SurvivalSkillsGetCommand implements CommandExecutor {
    public SurvivalSkillsGetCommand(SurvivalSkills plugin) {
        plugin.getCommand("ssget").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if (strings.length != 1) {
            p.sendRawMessage(ChatColor.RED + "Usage: /ssget <item>");
            return false;
        }

        if (strings[0].equalsIgnoreCase("miningarmor")) {
            p.getInventory().addItem(ItemStackGenerator.getMiningHelmet());
            p.getInventory().addItem(ItemStackGenerator.getMiningChestplate());
            p.getInventory().addItem(ItemStackGenerator.getMiningLeggings());
            p.getInventory().addItem(ItemStackGenerator.getMiningBoots());
            p.sendRawMessage(ChatColor.GREEN + "You have received: Mining Armor");
            p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            return true;
        }
        else if (strings[0].equalsIgnoreCase("wandererarmor")) {
            p.getInventory().addItem(ItemStackGenerator.getWandererHelmet());
            p.getInventory().addItem(ItemStackGenerator.getWandererChestplate());
            p.getInventory().addItem(ItemStackGenerator.getWandererLeggings());
            p.getInventory().addItem(ItemStackGenerator.getWandererBoots());
            p.sendRawMessage(ChatColor.GREEN + "You have received: Wanderer Armor");
            p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            return true;
        }
        else if (strings[0].equalsIgnoreCase("travellerarmor")) {
            p.getInventory().addItem(ItemStackGenerator.getTravellerHelmet());
            p.getInventory().addItem(ItemStackGenerator.getTravellerChestplate());
            p.getInventory().addItem(ItemStackGenerator.getTravellerLeggings());
            p.getInventory().addItem(ItemStackGenerator.getTravellerBoots());
            p.sendRawMessage(ChatColor.GREEN + "You have received: Traveller Armor");
            p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            return true;
        }
        else if (strings[0].equalsIgnoreCase("adventurerarmor")) {
            p.getInventory().addItem(ItemStackGenerator.getAdventurerHelmet());
            p.getInventory().addItem(ItemStackGenerator.getAdventurerChestplate());
            p.getInventory().addItem(ItemStackGenerator.getAdventurerLeggings());
            p.getInventory().addItem(ItemStackGenerator.getAdventurerBoots());
            p.sendRawMessage(ChatColor.GREEN + "You have received: Adventurer Armor");
            p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            return true;
        }

        ItemStack item = getCorrectItem(strings[0]);
        if (item == null) {
            p.sendRawMessage(ChatColor.RED + "Invalid item: " + strings[0]);
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            Bukkit.getLogger().warning("Item " + item.getType() + " does not have item meta");
            return false;
        }

        p.getInventory().addItem(item);
        p.sendRawMessage(ChatColor.GREEN + "You have received: " + meta.getDisplayName());
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        return true;
    }

    public ItemStack getCorrectItem(String item) {
        switch (item.toLowerCase()) {
            case "unlimitedtorch":
                return ItemStackGenerator.getUnlimitedTorch();
            case "jumpingboots":
                return ItemStackGenerator.getJumpingBoots();
            case "miningboots":
                return ItemStackGenerator.getMiningBoots();
            case "miningleggings":
                return ItemStackGenerator.getMiningLeggings();
            case "miningchestplate":
                return ItemStackGenerator.getMiningChestplate();
            case "mininghelmet":
                return ItemStackGenerator.getMiningHelmet();
            case "wandererboots":
                return ItemStackGenerator.getWandererBoots();
            case "wandererleggings":
                return ItemStackGenerator.getWandererLeggings();
            case "wandererchestplate":
                return ItemStackGenerator.getWandererChestplate();
            case "wandererhelmet":
                return ItemStackGenerator.getWandererHelmet();
            case "cavefinder":
                return ItemStackGenerator.getCaveFinder();
            case "travellerboots":
                return ItemStackGenerator.getTravellerBoots();
            case "travellerleggings":
                return ItemStackGenerator.getTravellerLeggings();
            case "travellerchestplate":
                return ItemStackGenerator.getTravellerChestplate();
            case "travellerhelmet":
                return ItemStackGenerator.getTravellerHelmet();
            case "adventurerboots":
                return ItemStackGenerator.getAdventurerBoots();
            case "adventurerleggings":
                return ItemStackGenerator.getAdventurerLeggings();
            case "adventurerchestplate":
                return ItemStackGenerator.getAdventurerChestplate();
            case "adventurerhelmet":
                return ItemStackGenerator.getAdventurerHelmet();
            case "wateringcan":
                return ItemStackGenerator.getWateringCan();
            case "unlimitedbonemeal":
                return ItemStackGenerator.getUnlimitedBoneMeal();
            case "harvester":
                return ItemStackGenerator.getHarvester();
            case "giantsummoner":
                return ItemStackGenerator.getGiantSummoner();
            case "broodmothersummoner":
                return ItemStackGenerator.getBroodMotherSummoner();
            case "exiledsummoner":
                return ItemStackGenerator.getVillagerSummoner();
            case "sortofstonepick":
                return ItemStackGenerator.getSortOfStonePick();
            case "fireworkcannon":
                return ItemStackGenerator.getFireworkCannon();
            case "sortwand":
                return ItemStackGenerator.getSortWand();
            case "unlimitedtropicalfishbucket":
                return ItemStackGenerator.getUnlimitedTropicalFishBucket();
            default:
                return null;
        }
    }
}
