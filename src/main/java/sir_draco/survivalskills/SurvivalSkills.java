package sir_draco.survivalskills;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import sir_draco.survivalskills.Abilities.AbilityTimer;
import sir_draco.survivalskills.Abilities.AutoTrash;
import sir_draco.survivalskills.Abilities.TrailEffect;
import sir_draco.survivalskills.Commands.*;
import sir_draco.survivalskills.Rewards.PlayerRewards;
import sir_draco.survivalskills.Rewards.Reward;
import sir_draco.survivalskills.SkillListeners.*;
import sir_draco.survivalskills.Trophy.Trophy;

import java.io.*;
import java.util.*;

public final class SurvivalSkills extends JavaPlugin {

    private final HashMap<UUID, ArrayList<Skill>> playerSkills = new HashMap<>();
    private final HashMap<UUID, Boolean> toggledScoreboard = new HashMap<>();
    private final HashMap<Player, Scoreboard> scoreboardTracker = new HashMap<>();
    private final HashMap<UUID, HashMap<String, Boolean>> trophyTracker = new HashMap<>();
    private final HashMap<Location, Trophy> trophies = new HashMap<>();
    private final HashMap<Integer, ItemStack> trophyItems = new HashMap<>();
    private final HashMap<Player, PlayerRewards> rewardTracker = new HashMap<>();
    private final HashMap<Player, ArrayList<AbilityTimer>> timerTracker = new HashMap<>();
    private final HashMap<Player, TrailEffect> trailTracker = new HashMap<>();
    private final HashMap<String, Particle> trails = new HashMap<>();
    private final HashMap<UUID, LeaderboardPlayer> leaderboardTracker = new HashMap<>();
    private final ArrayList<Material> farmingList = new ArrayList<>();
    private final ArrayList<NamespacedKey> recipeKeys = new ArrayList<>();

    private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private PlayerRewards playerRewards; // Holds the default information for rewards
    private BuildingSkill buildingListener;
    private MiningSkill miningListener;
    private FishingSkill fishingListener;
    private ExploringSkill exploringListener;
    private FarmingSkill farmingListener;
    private FightingSkill fightingListener;
    private CraftingSkill craftingListener;
    private MainSkill mainListener;
    private PlayerListener playerListener;
    private double buildingXP;
    private double miningXP;
    private double fishingXP;
    private double exploringXP;
    private double farmingXP;
    private double fightingXP;
    private double craftingXP;
    private double multiplier = 1;
    private FileConfiguration config;
    private File dataFile;
    private FileConfiguration data;
    private File trophyFile;
    private FileConfiguration trophyData;
    private File leaderboardFile;
    private FileConfiguration leaderboardData;
    private File permaTrashFile;
    private FileConfiguration permaTrashData;
    private boolean woolRecipes = false;
    private boolean griefPreventionEnabled = false;

    @Override
    public void onEnable() {
        // Make sure there are no stragglers from before
        World world = Bukkit.getWorld("world");
        if (world != null) {
            for (Entity ent : world.getEntities()) {
                if (!ent.getType().equals(EntityType.ITEM)) continue;
                Item item = (Item) ent;
                if (item.getOwner() == null) continue;
                if (item.getOwner().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) item.remove();
            }
        }

        // See if the config has ever been saved before
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        if (config.get("Version") == null || config.getDouble("Version") != 1.91) saveResource("config.yml", true);
        loadConfigSettings(); // XP amounts per action

        dataFile = new File(getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) saveResource("playerdata.yml", true);
        data = YamlConfiguration.loadConfiguration(dataFile);

        trophyFile = new File(getDataFolder(), "trophydata.yml");
        if (!trophyFile.exists()) saveResource("trophydata.yml", true);
        trophyData = YamlConfiguration.loadConfiguration(trophyFile);

        leaderboardFile = new File(getDataFolder(), "leaderboard.yml");
        if (!leaderboardFile.exists()) saveResource("leaderboard.yml", true);
        leaderboardData = YamlConfiguration.loadConfiguration(leaderboardFile);
        loadLeaderboard();

        permaTrashFile = new File(getDataFolder(), "permatrash.yml");
        if (!permaTrashFile.exists()) saveResource("permatrash.yml", true);
        permaTrashData = YamlConfiguration.loadConfiguration(permaTrashFile);

        loadListeners();

        loadTrophies();
        RecipeMaker.trophyRecipes(this);
        RecipeMaker.rewardRecipes(this);
        loadCommands();
        createTrails();

        if (!getServer().getOnlinePlayers().isEmpty()) {
            for (Player p : getServer().getOnlinePlayers()) {
                loadData(p, false);
                timerTracker.put(p, new ArrayList<>());
                if (toggledScoreboard.get(p.getUniqueId())) initializeScoreboard(p);
                else hideScoreboard(p);
                for (Map.Entry<Location, Trophy> trophy : getTrophies().entrySet()) {
                    Location loc = trophy.getKey();
                    if (loc.getWorld() == null) continue;
                    if (!loc.getWorld().equals(p.getWorld())) continue;
                    if (p.getLocation().distance(loc) > 50) continue;
                    trophy.getValue().getEffects().checkForPlayers();
                }
                loadPlayerRewards(p);
                checkMainXP(p);
                loadPermaTrash(p);
            }
        }

        if (getServer().getPluginManager().getPlugin("GriefPrevention") != null) griefPreventionEnabled = true;
        runSkillAutoSave();
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.closeInventory();
            savePermaTrash(p);
        }

        try {
            savePlayerData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            saveTrophies();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            mainListener.saveGraves();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            saveLeaderboard();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getMiningListener().endSpelunkerAll();
    }

    /**
     * Gets the XP amounts per action for each skill from the config
     */
    public void loadConfigSettings() {
        buildingXP = config.getDouble("BuildingXP");
        miningXP = config.getDouble("MiningXP");
        fishingXP = config.getDouble("FishingXP");
        exploringXP = config.getDouble("ExploringXP");
        farmingXP = config.getDouble("FarmingXP");
        fightingXP = config.getDouble("FightingXP");
        craftingXP = config.getDouble("CraftingXP");

        if (config.get("SkillXPMultiplier") != null) multiplier = config.getDouble("SkillXPMultiplier");

        playerRewards = new PlayerRewards();
        loadRewardConfig("Mining");
        loadRewardConfig("Exploring");
        loadRewardConfig("Farming");
        loadRewardConfig("Building");
        loadRewardConfig("Fighting");
        loadRewardConfig("Fishing");
        loadRewardConfig("Crafting");
        loadRewardConfig("Main");
    }

    public void loadRewardConfig(String type) {
        ConfigurationSection section = config.getConfigurationSection(type);
        if (section == null) return;
        section.getKeys(false).forEach(key -> {
            boolean enabled = config.getBoolean(type + "." + key + ".Enabled");
            int level = config.getInt(type + "." + key + ".Level");
            String rewardType = config.getString(type + "." + key + ".Type");
            playerRewards.addReward(type, new Reward(type, key, rewardType, level, enabled));
        });
    }

    public void loadCommands() {
        new SkillStatsCommand(this);
        new ToggleScoreboardCommand(this);
        new SpelunkerCommand(this);
        new VeinminerCommand(this);
        new NightVisionCommand(this);
        new PeacefulMinerCommand(this);
        new AutoEatCommand(this);
        new EatCommand(this);
        new FlightCommand(this);
        new MobScannerCommand(this);
        new WaterBreathingCommand(this);
        new DeathLocationCommand(this);
        new ToggleSpeedCommand(this);
        new ToggleTrailCommand(this);
        new AutoTrashCommand(this);
        new PermaTrashCommand(this);
        new TogglePhantomsCommand(this);
        new DeathReturnCommand(this);

        new GetTrophyCommand(this);
        new SurvivalSkillsGetCommand(this);
        new CaveFinderCommand(this);
        new BossCommand(this);
        new SurvivalSkillsCommand(this);
        new SkillsMultiplierCommand(this);
        new ResetFirstDragon(this);
        new BossMusicCommand(this);
    }

    public void loadLeaderboard() {
        ConfigurationSection section = leaderboardData.getConfigurationSection("");
        if (section == null) return;
        section.getKeys(false).forEach(key -> {
            String name = leaderboardData.getString(key + ".Name");
            int level = leaderboardData.getInt(key + ".Level");
            int building = leaderboardData.getInt(key + ".Building");
            int mining = leaderboardData.getInt(key + ".Mining");
            int fishing = leaderboardData.getInt(key + ".Fishing");
            int exploring = leaderboardData.getInt(key + ".Exploring");
            int farming = leaderboardData.getInt(key + ".Farming");
            int fighting = leaderboardData.getInt(key + ".Fighting");
            int crafting = leaderboardData.getInt(key + ".Crafting");
            int main = leaderboardData.getInt(key + ".Main");
            int deaths = leaderboardData.getInt(key + ".Deaths");
            LeaderboardPlayer leaderboard = new LeaderboardPlayer(name, level, building, mining, fishing, exploring,
                    farming, fighting, crafting, main, deaths);
            leaderboardTracker.put(UUID.fromString(key), leaderboard);
        });
    }

    public void loadPermaTrash(Player p) {
        UUID uuid = p.getUniqueId();
        ConfigurationSection perma = permaTrashData.getConfigurationSection(uuid.toString());
        if (perma == null) return;

        ConfigurationSection materials = permaTrashData.getConfigurationSection(uuid + ".Materials");
        AutoTrash trash = new AutoTrash(false);
        if (materials != null) {
            // get the list of materials from the config
            materials.getKeys(false).forEach(key -> {
                String type = permaTrashData.getString(uuid + ".Materials." + key);
                if (type != null) {
                    Material material = Material.getMaterial(type);
                    if (material == null) {
                        Bukkit.getLogger().warning("Material " + key + " for " + uuid + " is not valid");
                        return;
                    }
                    trash.addTrashItem(new ItemStack(material));
                }
                else Bukkit.getLogger().warning("Material " + key + " for " + uuid + " is not valid");
            });
        }

        ConfigurationSection enchants = permaTrashData.getConfigurationSection(uuid + ".Enchants");
        if (enchants != null) {
            ArrayList<String> keyNames = new ArrayList<>();
            // get the list of items from the config
            enchants.getKeys(false).forEach(key -> {
                String keyName = permaTrashData.getString(uuid + ".Enchants." + key);
                keyNames.add(keyName);
            });

            for (String key : keyNames) {
                Enchantment enchant = getEnchantFromKey(key);
                if (enchant == null) {
                    Bukkit.getLogger().warning("Enchantment " + key + " is not valid");
                    return;
                }

                ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                if (meta == null) return;
                meta.addStoredEnchant(enchant, 1, false);
                item.setItemMeta(meta);
                trash.addTrashItem(item);
            }
        }

        getFishingListener().getPermaTrash().put(p, trash);
    }

    public void savePlayerData(Player p) {
        if (playerSkills.isEmpty()) return;
        if (data == null) return;

        UUID uuid = p.getUniqueId();
        if (trophyTracker.containsKey(uuid)) {
            for (Map.Entry<String, Boolean> list : trophyTracker.get(uuid).entrySet())
                data.set(uuid + "." + list.getKey(), list.getValue());
        }
        else Bukkit.getLogger().warning("Player " + p.getName() + " does not have a trophy status");

        if (toggledScoreboard.containsKey(uuid)) data.set(uuid + ".Scoreboard", toggledScoreboard.get(uuid));
        else Bukkit.getLogger().warning("Player " + p.getName() + " does not have a scoreboard status");

        if (getFightingListener().getNoPhantomSpawns().contains(p)) data.set(uuid + ".NoPhantoms", true);
        else data.set(uuid + ".NoPhantoms", false);

        if (trailTracker.containsKey(p)) data.set(uuid + ".Trail", trailTracker.get(p).getTrailName());
        else data.set(uuid + ".Trail", "None");

        if (farmingListener.getAutoEat().contains(p)) data.set(uuid + ".AutoEat", true);
        else data.set(uuid + ".AutoEat", false);

        data.set(uuid + ".Veinminer", miningListener.getVeinminerTracker().getOrDefault(p, -1));

        if (miningListener.getPeacefulMiners().contains(p)) data.set(uuid + ".PeacefulMiner", true);
        else data.set(uuid + ".PeacefulMiner", false);

        if (playerSkills.containsKey(uuid)) {
            for (Skill skill : playerSkills.get(uuid)) {
                data.set(uuid + "." + skill.getSkillName() + ".Level", skill.getLevel());
                data.set(uuid + "." + skill.getSkillName() + ".Experience", skill.getExperience());
            }
        }
        else Bukkit.getLogger().warning("Player " + p.getName() + " does not have any skills");

        savePermaTrash(p);

        try {
            data.save(dataFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePlayerData() throws IOException {
        if (playerSkills.isEmpty()) return;
        if (data == null) return;

        for (Map.Entry<UUID, HashMap<String, Boolean>> player : trophyTracker.entrySet()) {
            UUID uuid = player.getKey();
            data.set(uuid + ".Scoreboard", toggledScoreboard.get(uuid));
            for (Map.Entry<String, Boolean> list : player.getValue().entrySet()) {
                data.set(uuid + "." + list.getKey(), list.getValue());
            }
        }

        for (Map.Entry<UUID, ArrayList<Skill>> player : playerSkills.entrySet()) {
            UUID uuid = player.getKey();
            for (Skill skill : player.getValue()) {
                data.set(uuid + "." + skill.getSkillName() + ".Level", skill.getLevel());
                data.set(uuid + "." + skill.getSkillName() + ".Experience", skill.getExperience());
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            if (toggledScoreboard.containsKey(uuid)) data.set(uuid + ".Scoreboard", toggledScoreboard.get(uuid));
            else Bukkit.getLogger().warning("Player " + p.getName() + " does not have a scoreboard status");

            if (getFightingListener().getNoPhantomSpawns().contains(p)) data.set(uuid + ".NoPhantoms", true);
            else data.set(uuid + ".NoPhantoms", false);

            if (trailTracker.containsKey(p)) data.set(uuid + ".Trail", trailTracker.get(p).getTrailName());
            else data.set(uuid + ".Trail", "None");

            if (farmingListener.getAutoEat().contains(p)) data.set(uuid + ".AutoEat", true);
            else data.set(uuid + ".AutoEat", false);

            data.set(uuid + ".Veinminer", miningListener.getVeinminerTracker().getOrDefault(p, -1));

            if (miningListener.getPeacefulMiners().contains(p)) data.set(uuid + ".PeacefulMiner", true);
            else data.set(uuid + ".PeacefulMiner", false);
        }

        data.save(dataFile);
    }

    public void saveLeaderboard() throws IOException {
        if (leaderboardTracker.isEmpty()) return;
        if (leaderboardData == null) return;

        for (Map.Entry<UUID, LeaderboardPlayer> player : leaderboardTracker.entrySet()) {
            leaderboardData.set(player.getKey() + ".Name", player.getValue().getName());
            leaderboardData.set(player.getKey() + ".Level", player.getValue().getScore());
            leaderboardData.set(player.getKey() + ".Building", player.getValue().getBuildingScore());
            leaderboardData.set(player.getKey() + ".Mining", player.getValue().getMiningScore());
            leaderboardData.set(player.getKey() + ".Fishing", player.getValue().getFishingScore());
            leaderboardData.set(player.getKey() + ".Exploring", player.getValue().getExploringScore());
            leaderboardData.set(player.getKey() + ".Farming", player.getValue().getFarmingScore());
            leaderboardData.set(player.getKey() + ".Fighting", player.getValue().getFightingScore());
            leaderboardData.set(player.getKey() + ".Crafting", player.getValue().getCraftingScore());
            leaderboardData.set(player.getKey() + ".Main", player.getValue().getMainScore());
            leaderboardData.set(player.getKey() + ".Deaths", player.getValue().getDeathScore());
        }

        try {
            leaderboardData.save(leaderboardFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePermaTrash(Player p) {
        if (permaTrashData == null) return;
        UUID uuid = p.getUniqueId();
        if (!getFishingListener().getPermaTrash().containsKey(p)) return;
        AutoTrash trash = getFishingListener().getPermaTrash().get(p);
        if (trash == null) return;

        int i = 0;
        if (trash.getTrashMaterials().isEmpty()) permaTrashData.set(uuid + ".Materials", null);
        for (Material mat : trash.getTrashMaterials()) {
            permaTrashData.set(uuid + ".Materials." + i, mat.toString());
            i++;
        }

        i = 0;
        if (trash.getEnchants().isEmpty()) permaTrashData.set(uuid + ".Enchants", null);
        for (Enchantment enchant : trash.getEnchants()) {
            permaTrashData.set(uuid + ".Enchants." + i, enchant.getKey().toString());
            i++;
        }

        try {
            permaTrashData.save(permaTrashFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateExploringStats(UUID uuid) {
        Skill exploring = getSkill(uuid, "Exploring");
        exploring.changeExperience(exploringListener.getPlayerSteps(uuid) * exploringXP, playerMaxSkillLevel(uuid));
    }

    public void saveTrophies() throws IOException {
        if (trophyData == null) return;

        if (trophies.isEmpty()) {
            saveResource("trophydata.yml", true);
            return;
        }

        for (Map.Entry<Location, Trophy> trophy : trophies.entrySet()) {
            trophy.getValue().shutdownTrophy();
            trophyData.set(trophy.getValue().getID() + ".Location", trophy.getKey());
            trophyData.set(trophy.getValue().getID() + ".UUID", trophy.getValue().getUUID().toString());
            trophyData.set(trophy.getValue().getID() + ".Type", trophy.getValue().getType());
            trophyData.set(trophy.getValue().getID() + ".PlayerName", trophy.getValue().getPlayerName());
        }

        trophyData.save(trophyFile);
    }

    /**
     * Intializes the listeners and registers them in the server
     */
    public void loadListeners() {
        createFarmingList();

        buildingListener = new BuildingSkill(this, buildingXP);
        miningListener = new MiningSkill(this, miningXP, config.getInt("VeinMinerHungerAmount"));
        fishingListener = new FishingSkill(this, fishingXP);
        exploringListener = new ExploringSkill(this, exploringXP);
        farmingListener = new FarmingSkill(this, farmingXP);
        fightingListener = new FightingSkill(this, fightingXP);
        craftingListener = new CraftingSkill(this, craftingXP);
        mainListener = new MainSkill(this);
        playerListener = new PlayerListener(this);
        TabCompleter tabCompleter = new TabCompleter(this);

        getServer().getPluginManager().registerEvents(buildingListener, this);
        getServer().getPluginManager().registerEvents(miningListener, this);
        getServer().getPluginManager().registerEvents(fishingListener, this);
        getServer().getPluginManager().registerEvents(exploringListener, this);
        getServer().getPluginManager().registerEvents(farmingListener, this);
        getServer().getPluginManager().registerEvents(fightingListener, this);
        getServer().getPluginManager().registerEvents(craftingListener, this);
        getServer().getPluginManager().registerEvents(mainListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(tabCompleter, this);
    }

    /**
     * Gets the player data that stores information about the experience of each skill
     * as well as whether the scoreboard is enabled or not and which trophies a player
     * has obtained.
     * Returns the boolean value of if the intended loading took place
     */
    public boolean loadData(Player p, boolean isNewPlayer) {
        HashMap<String, Boolean> trophyList = new HashMap<>();
        if (!data.contains(p.getUniqueId().toString())) {
            ArrayList<Skill> skills = new ArrayList<>();
            trophyList.put("CaveTrophy", false);
            trophyList.put("ForestTrophy", false);
            trophyList.put("FarmingTrophy", false);
            trophyList.put("OceanTrophy", false);
            trophyList.put("FishingTrophy", false);
            trophyList.put("ColorTrophy", false);
            trophyList.put("NetherTrophy", false);
            trophyList.put("EndTrophy", false);
            trophyList.put("ChampionTrophy", false);
            trophyList.put("GodTrophy", false);

            skills.add(new Skill(0, 1, "Main"));
            skills.add(new Skill(0, 1, "Building"));
            skills.add(new Skill(0, 1, "Mining"));
            skills.add(new Skill(0, 1, "Fishing"));
            skills.add(new Skill(0, 1, "Exploring"));
            skills.add(new Skill(0, 1, "Farming"));
            skills.add(new Skill(0, 1, "Fighting"));
            skills.add(new Skill(0, 1, "Crafting"));
            trophyTracker.put(p.getUniqueId(), trophyList);
            playerSkills.put(p.getUniqueId(), skills);
            toggledScoreboard.put(p.getUniqueId(), true);

            savePlayerData(p);
            return isNewPlayer;
        }

        UUID uuid = p.getUniqueId();
        if (!toggledScoreboard.containsKey(uuid)) loadScoreboardSetting(uuid);
        if (!trophyTracker.containsKey(uuid)) loadPlayerTrophies(uuid);
        if (!playerSkills.containsKey(uuid)) loadPlayerSkills(uuid);

        if (data.get(uuid + ".NoPhantoms") != null) {
            boolean phantoms = data.getBoolean(uuid + ".NoPhantoms");
            if (phantoms && !getFightingListener().getNoPhantomSpawns().contains(p)) {
                getFightingListener().getNoPhantomSpawns().add(p);
            }
        }

        if (data.get(uuid + ".Trail") != null) {
            String trailName = data.getString(uuid + ".Trail");
            if (trailName != null && !trailName.equals("None")) {
                if (trails.containsKey(trailName)) {
                    int dustType = 1;
                    if (trailName.equalsIgnoreCase("Dust")) dustType = 2;
                    else if (trailName.equalsIgnoreCase("Rainbow")) dustType = 3;
                    TrailEffect effect = new TrailEffect(p, trails.get(trailName), dustType, trailName);
                    effect.runTaskTimer(this, 60, 1);
                    trailTracker.put(p, effect);
                }
            }
        }

        if (data.get(uuid + ".AutoEat") != null) {
            boolean autoEat = data.getBoolean(uuid + ".AutoEat");
            if (autoEat && !farmingListener.getAutoEat().contains(p)) farmingListener.getAutoEat().add(p);
        }

        if (data.get(uuid + ".Veinminer") != null) {
            int veinminer = data.getInt(uuid + ".Veinminer");
            if (veinminer == 0 || veinminer == 1) miningListener.getVeinminerTracker().put(p, veinminer);
        }

        if (data.get(uuid + ".PeacefulMiner") != null) {
            boolean peacefulMiner = data.getBoolean(uuid + ".PeacefulMiner");
            if (peacefulMiner && !miningListener.getPeacefulMiners().contains(p)) miningListener.getPeacefulMiners().add(p);
        }

        return !isNewPlayer;
    }

    public void loadScoreboardSetting(UUID uuid) {
        toggledScoreboard.put(uuid, data.getBoolean(uuid + ".Scoreboard"));
    }

    public void loadPlayerTrophies(UUID uuid) {
        HashMap<String, Boolean> trophyList = new HashMap<>();
        trophyList.put("CaveTrophy", data.getBoolean(uuid + ".CaveTrophy"));
        trophyList.put("ForestTrophy", data.getBoolean(uuid + ".ForestTrophy"));
        trophyList.put("FarmingTrophy", data.getBoolean(uuid + ".FarmingTrophy"));
        trophyList.put("OceanTrophy", data.getBoolean(uuid + ".OceanTrophy"));
        trophyList.put("FishingTrophy", data.getBoolean(uuid + ".FishingTrophy"));
        trophyList.put("ColorTrophy", data.getBoolean(uuid + ".ColorTrophy"));
        trophyList.put("NetherTrophy", data.getBoolean(uuid + ".NetherTrophy"));
        trophyList.put("EndTrophy", data.getBoolean(uuid + ".EndTrophy"));
        trophyList.put("ChampionTrophy", data.getBoolean(uuid + ".ChampionTrophy"));
        trophyList.put("GodTrophy", data.getBoolean(uuid + ".GodTrophy"));
        trophyTracker.put(uuid, trophyList);
    }

    public void loadPlayerSkills(UUID uuid) {
        ArrayList<Skill> skills = new ArrayList<>();
        skills.add(new Skill(data.getDouble(uuid + ".Main.Experience"), data.getInt(uuid + ".Main.Level"), "Main"));
        skills.add(new Skill(data.getDouble(uuid + ".Building.Experience"), data.getInt(uuid + ".Building.Level"), "Building"));
        skills.add(new Skill(data.getDouble(uuid + ".Mining.Experience"), data.getInt(uuid + ".Mining.Level"), "Mining"));
        skills.add(new Skill(data.getDouble(uuid + ".Fishing.Experience"), data.getInt(uuid + ".Fishing.Level"), "Fishing"));
        skills.add(new Skill(data.getDouble(uuid + ".Exploring.Experience"), data.getInt(uuid + ".Exploring.Level"), "Exploring"));
        skills.add(new Skill(data.getDouble(uuid + ".Farming.Experience"), data.getInt(uuid + ".Farming.Level"), "Farming"));
        skills.add(new Skill(data.getDouble(uuid + ".Fighting.Experience"), data.getInt(uuid + ".Fighting.Level"), "Fighting"));
        skills.add(new Skill(data.getDouble(uuid + ".Crafting.Experience"), data.getInt(uuid + ".Crafting.Level"), "Crafting"));
        playerSkills.put(uuid, skills);
    }

    /**
     * Gets the location of trophies, their type, and
     * who placed the trophy from a file
     */
    public void loadTrophies() {
        ConfigurationSection section = trophyData.getConfigurationSection("");
        if (section == null) return;

        section.getKeys(false).forEach(key -> {
            Location loc = trophyData.getLocation(key + ".Location");
            String uuidString = trophyData.getString(key + ".UUID");
            if (uuidString == null) {
                trophyData.set(key, null);
            }
            else {
                int id = Integer.parseInt(key);
                UUID uuid = UUID.fromString(uuidString);
                String type = trophyData.getString(key + ".Type");
                String playerName = trophyData.getString(key + ".PlayerName");
                Trophy trophy = new Trophy(loc, uuid, type, id, playerName);
                trophy.spawnTrophy(this);
                trophies.put(loc, trophy);
            }
        });
    }

    public void loadPlayerRewards(Player p) {
        if (playerRewards.getRewardList() == null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    loadData(p, false);
                    loadPlayerRewards(p);
                }
            }.runTaskLater(this, 40);
            return;
        }

        if (rewardTracker.containsKey(p)) return;
        rewardTracker.put(p, new PlayerRewards(playerRewards.getRewardList()));
        rewardTracker.get(p).enableRewards(p, playerSkills.get(p.getUniqueId()));
    }

    public void removeTrophy(Location loc) {
        Trophy trophy = trophies.get(loc);
        trophies.remove(loc);
        if (trophyData.get("" + trophy.getID()) == null) return;
        trophyData.set("" + trophy.getID(), null);
    }

    public int getTrophyCount(UUID uuid) {
        int count = 0;
        for (Map.Entry<String, Boolean> list : trophyTracker.get(uuid).entrySet()) if (list.getValue()) count++;
        return count;
    }

    /**
     * Gets the specified skill of a player
     */
    public Skill getSkill(UUID uuid, String skillName) {
        if (playerSkills.isEmpty()) {
            ArrayList<Skill> skills = new ArrayList<>();
            skills.add(new Skill(0, 0, skillName));
            playerSkills.put(uuid, skills);
        }
        if (playerSkills.get(uuid) == null) return new Skill(0, 0, skillName);
        for (Skill skill : playerSkills.get(uuid)) if (skill.getSkillName().equalsIgnoreCase(skillName)) return skill;
        Skill skill = new Skill(0, 0, skillName);
        playerSkills.get(uuid).add(skill);
        return skill;
    }

    public void initializeScoreboard(Player p) {
        if (scoreboardManager == null) {
            scoreboardManager = Bukkit.getScoreboardManager();
            if (scoreboardManager == null) return;
        }
        Scoreboard board = scoreboardManager.getNewScoreboard();
        p.setScoreboard(board);
        scoreboardTracker.put(p, board);
        if (board.getObjective("Main") == null) {
            Objective main = board.registerNewObjective("Main", Criteria.DUMMY, "Main");
            Objective deaths = board.registerNewObjective("Deaths", Criteria.DUMMY, "Deaths");
            main.setDisplaySlot(DisplaySlot.SIDEBAR);
            deaths.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            updateScoreboard(p, "Main");
        }
        else updateScoreboard(p, "Main");
        p.setScoreboard(board);
        scoreboardTracker.put(p, board);
    }

    /**
     * Creates an empty scoreboard to hide an existing scoreboard
     */
    public void hideScoreboard(Player p) {
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        if (scoreboardManager == null) return;
        Scoreboard empty = scoreboardManager.getNewScoreboard();
        p.setScoreboard(empty);
        scoreboardTracker.remove(p);
    }

    /**
     * Changes a player's scoreboard to represent a change in the XP of a skill
     */
    public void updateScoreboard(Player p, String skillName) {
        // Potentially add a cool-down every 2 seconds
        if (toggledScoreboard.get(p.getUniqueId()) == null) {
            toggledScoreboard.put(p.getUniqueId(), true);
            initializeScoreboard(p);
            return;
        }
        if (!toggledScoreboard.get(p.getUniqueId())) return;
        Scoreboard board = scoreboardTracker.get(p);
        if (board == null) {
            initializeScoreboard(p);
            return;
        }
        Skill mainSkill = getSkill(p.getUniqueId(), "Main");
        Objective main = board.getObjective("Main");
        Objective deaths = board.getObjective("Deaths");
        if (main == null) return;
        if (deaths == null) return;
        int playerLevel = mainSkill.getLevel();

        main.setDisplayName(ChatColor.GOLD + "Player Main Level " + ChatColor.AQUA + "(" + playerLevel + ")");

        // Deaths
        for (Player player : Bukkit.getOnlinePlayers()) {
            LeaderboardPlayer leaderboardPlayer = leaderboardTracker.get(player.getUniqueId());
            if (leaderboardPlayer != null) deaths.getScore(player.getName()).setScore(leaderboardPlayer.getDeathScore());
            else deaths.getScore(player.getName()).setScore(0);
        }

        // NameTag
        for (Player player : Bukkit.getOnlinePlayers()) {
            Skill playerMainSkill = getSkill(player.getUniqueId(), "Main");
            ChatColor color = getChatColor(playerMainSkill);
            String colorString;
            if (playerMainSkill.getLevel() == 100) colorString = ChatColor.BOLD.toString() + color;
            else colorString = color.toString();
            Team team = board.getTeam(player.getName());
            if (team == null) {
                team = board.registerNewTeam(player.getName());
                team.addEntry(player.getName());
                team.setPrefix(colorString + "(" + playerMainSkill.getLevel() + ") ");
            }
            else team.setPrefix(colorString + "(" + playerMainSkill.getLevel() + ") ");
        }

        int score = 1;
        if (skillName.equals("Main")) return;
        Skill sideSkill = getSkill(p.getUniqueId(), skillName);
        int skillLevel = sideSkill.getLevel();
        String skillXPNext;
        if (skillLevel < 100) {
            double ratio = (double) sideSkill.getExperienceSoFarInLevel() / sideSkill.getRawExperienceForNextLevel();
            int newRatio = (int) (ratio * 100);
            skillXPNext = "Progress: " + ChatColor.AQUA + "(" + newRatio + "％)";
        }
        else skillXPNext = "Progress: MAX";
        score = newTeam(board, main, "SkillXPNext", ChatColor.BLUE.toString(), ChatColor.GRAY + skillXPNext, score);
        score = newTeam(board, main, "Skill", ChatColor.GRAY.toString(), ChatColor.GOLD + skillName + " Level " + ChatColor.AQUA + "(" + skillLevel + ")", score);
        newTeam(board, main, "Empty", ChatColor.DARK_PURPLE.toString(), ChatColor.GRAY + "----------------", score);
    }

    private static ChatColor getChatColor(Skill playerMainSkill) {
        int playerMainLevel = playerMainSkill.getLevel();
        ChatColor color;
        if (playerMainLevel < 10) color = ChatColor.DARK_GRAY;
        if (playerMainLevel < 20) color = ChatColor.GRAY;
        else if (playerMainLevel < 30) color = ChatColor.GREEN;
        else if (playerMainLevel < 40) color = ChatColor.DARK_GREEN;
        else if (playerMainLevel < 50) color = ChatColor.AQUA;
        else if (playerMainLevel < 60) color = ChatColor.DARK_AQUA;
        else if (playerMainLevel < 70) color = ChatColor.LIGHT_PURPLE;
        else if (playerMainLevel < 80) color = ChatColor.DARK_PURPLE;
        else if (playerMainLevel < 90) color = ChatColor.RED;
        else if (playerMainLevel < 100) color = ChatColor.DARK_RED;
        else color = ChatColor.GOLD;
        return color;
    }

    public int newTeam(Scoreboard board, Objective obj, String name, String holder, String display, int scoreCount) {
        Team team = board.getTeam(name);
        if (team == null) {
            team = board.registerNewTeam(name);
            team.addEntry(holder);
            team.setPrefix(display);
        }
        else team.setPrefix(display);

        obj.getScore(holder).setScore(scoreCount);
        return scoreCount + 1;
    }

    public void createFarmingList() {
        //farmingList.add(Material.SUGAR_CANE); // Needs to be bug prevented || cactus, bamboo, seaweed
        farmingList.add(Material.SUGAR_CANE);
        farmingList.add(Material.CACTUS);
        farmingList.add(Material.KELP_PLANT);
        farmingList.add(Material.KELP);
        farmingList.add(Material.WHEAT);
        farmingList.add(Material.WHEAT_SEEDS);
        farmingList.add(Material.CARROTS);
        farmingList.add(Material.CARROT);
        farmingList.add(Material.POTATOES);
        farmingList.add(Material.POTATO);
        farmingList.add(Material.BEETROOTS);
        farmingList.add(Material.BEETROOT_SEEDS);
        farmingList.add(Material.MELON);
        farmingList.add(Material.MELON_SEEDS);
        farmingList.add(Material.PUMPKIN);
        farmingList.add(Material.PUMPKIN_SEEDS);
        farmingList.add(Material.COCOA_BEANS);
        farmingList.add(Material.COCOA);
        farmingList.add(Material.BROWN_MUSHROOM_BLOCK);
        farmingList.add(Material.BROWN_MUSHROOM);
        farmingList.add(Material.RED_MUSHROOM_BLOCK);
        farmingList.add(Material.RED_MUSHROOM);
        farmingList.add(Material.NETHER_WART);
    }

    /**
     * Removes the player from the skills list and from the
     * scoreboard hashtable
     */
    public void playerQuit(Player p) {
        playerSkills.remove(p.getUniqueId());
        toggledScoreboard.remove(p.getUniqueId());
        scoreboardTracker.remove(p);
        if (trailTracker.containsKey(p)) {
            trailTracker.get(p).cancel();
            trailTracker.remove(p);
        }
        getFightingListener().getNoPhantomSpawns().remove(p);
        fightingListener.getActiveBerserkers().remove(p);
    }

    /**
     * Counts how many trophies a player has obtained and returns the
     * max level a player can be
     */
    public int playerMaxSkillLevel(UUID uuid) {
        int count = 0;
        if (trophyTracker.get(uuid) == null)  {
            new BukkitRunnable() {
                @Override
                public void run() {
                    loadTrophies();
                }
            }.runTaskLater(this, 20);
            return 10;
        }
        for (Map.Entry<String, Boolean> list : trophyTracker.get(uuid).entrySet()) if (list.getValue()) count++;
        return 10 + (count * 10);
    }

    public int generateID() {
        int id = (int) Math.ceil(Math.random() * 1000000);
        if (trophies.isEmpty()) return id;
        for (Map.Entry<Location, Trophy> trophy : trophies.entrySet()) if (trophy.getValue().getID() == id) return generateID();
        return id;
    }

    public int getRewardLevel(String type, String reward) {
        for (Reward r : playerRewards.getRewardList().get(type)) {
            if (r.getName().equalsIgnoreCase(reward)) return r.getLevel();
        }
        return 0;
    }

    public ArrayList<Material> getFarmingList() {
        return farmingList;
    }

    public HashMap<UUID, Boolean> getToggledScoreboard() {
        return toggledScoreboard;
    }

    public HashMap<Location, Trophy> getTrophies() {
        return trophies;
    }

    public ItemStack getTrophyItem(int type) {
        if (!trophyItems.containsKey(type)) return new ItemStack(Material.AIR);
        return trophyItems.get(type);
    }

    public HashMap<Integer, ItemStack> getTrophyItems() {
        return trophyItems;
    }

    public HashMap<UUID, ArrayList<Skill>> getPlayerSkills() {
        return playerSkills;
    }

    public PlayerRewards getPlayerRewards(Player p) {
        if (!rewardTracker.containsKey(p)) {
            loadPlayerRewards(p);
            return rewardTracker.get(p);
        }
        return rewardTracker.get(p);
    }

    public HashMap<Player, ArrayList<AbilityTimer>> getTimerTracker() {
        return timerTracker;
    }

    public void endPlayerTimers(Player p) {
        if (!timerTracker.containsKey(p)) return;
        for (AbilityTimer timer : timerTracker.get(p)) timer.endAbility();
    }

    public void addAbility(Player p, AbilityTimer timer) {
        timerTracker.computeIfAbsent(p, k -> new ArrayList<>());
        timerTracker.get(p).add(timer);
    }

    public void removeAbility(Player p, String ability) {
        if (!timerTracker.containsKey(p)) return;
        timerTracker.get(p).removeIf(timer -> timer.getName().equalsIgnoreCase(ability));
    }

    public AbilityTimer getAbility(Player p, String ability) {
        if (!timerTracker.containsKey(p)) return null;
        for (AbilityTimer timer : timerTracker.get(p)) if (timer.getName().equalsIgnoreCase(ability)) return timer;
        return null;
    }

    public void checkMainXP(Player p) {
        if (!playerSkills.containsKey(p.getUniqueId())) return;
        double totalXP = 0;
        for (Skill skill : playerSkills.get(p.getUniqueId())) {
            if (skill.getSkillName().equalsIgnoreCase("Main")) continue;
            totalXP += skill.getExperience();
        }
        
        totalXP /= 7.0;
        Skill main = getSkill(p.getUniqueId(), "Main");
        if (main.getExperience() < totalXP || main.getExperience() > totalXP + 10.0) {
            main.setExperience((int) totalXP);
            savePlayerData(p);
        }
    }

    /**
     * Returns true if there is a claim there
     */
    public boolean checkForClaim(Player p, Location loc) {
        String noBuildReason = GriefPrevention.instance.allowBuild(p, loc);
        return (noBuildReason != null);
    }

    public ArrayList<String> getTopTen() {
        // Get the 10 highest scores from the leaderboard
        ArrayList<String> topTen = new ArrayList<>();
        HashMap<UUID, LeaderboardPlayer> topTenPlayers = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            UUID topPlayer = null;
            String name = "";
            double topScore = 0;
            for (Map.Entry<UUID, LeaderboardPlayer> entry : leaderboardTracker.entrySet()) {
                if (topTenPlayers.containsKey(entry.getKey())) continue;
                if (entry.getValue().getScore() > topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getScore();
                    name = entry.getValue().getName();
                }
            }
            if (topPlayer == null) break;
            topTenPlayers.put(topPlayer, leaderboardTracker.get(topPlayer));
            topTen.add(ChatColor.AQUA.toString() + i + ": " + ChatColor.GOLD + name + ChatColor.AQUA + " - " + ChatColor.GREEN + topScore);
        }
        return topTen;
    }

    public ArrayList<String> sortLeaderboard(String skillName) {
        // Get the 10 highest scores from the leaderboard
        ArrayList<String> sorted = new ArrayList<>();
        HashMap<UUID, LeaderboardPlayer> sortedPlayers = new HashMap<>();
        for (int i = 1; i <= leaderboardTracker.size(); i++) {
            UUID topPlayer = null;
            String name = "";
            double topScore = 0;
            if (skillName.equalsIgnoreCase("deaths")) topScore = Integer.MAX_VALUE;
            for (Map.Entry<UUID, LeaderboardPlayer> entry : leaderboardTracker.entrySet()) {
                if (sortedPlayers.containsKey(entry.getKey())) continue;

                if (skillName.equalsIgnoreCase("all") && entry.getValue().getScore() > topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getScore();
                    name = entry.getValue().getName();
                }
                else if (skillName.equalsIgnoreCase("building") && entry.getValue().getBuildingScore() > topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getBuildingScore();
                    name = entry.getValue().getName();
                }
                else if (skillName.equalsIgnoreCase("crafting") && entry.getValue().getCraftingScore() > topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getCraftingScore();
                    name = entry.getValue().getName();
                }
                else if (skillName.equalsIgnoreCase("exploring") && entry.getValue().getExploringScore() > topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getExploringScore();
                    name = entry.getValue().getName();
                }
                else if (skillName.equalsIgnoreCase("farming") && entry.getValue().getFarmingScore() > topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getFarmingScore();
                    name = entry.getValue().getName();
                }
                else if (skillName.equalsIgnoreCase("fighting") && entry.getValue().getFightingScore() > topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getFightingScore();
                    name = entry.getValue().getName();
                }
                else if (skillName.equalsIgnoreCase("fishing") && entry.getValue().getFishingScore() > topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getFishingScore();
                    name = entry.getValue().getName();
                }
                else if (skillName.equalsIgnoreCase("mining") && entry.getValue().getMiningScore() > topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getMiningScore();
                    name = entry.getValue().getName();
                }
                else if (skillName.equalsIgnoreCase("main") && entry.getValue().getMainScore() > topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getMainScore();
                    name = entry.getValue().getName();
                }
                else if (skillName.equalsIgnoreCase("deaths") && entry.getValue().getDeathScore() < topScore) {
                    topPlayer = entry.getKey();
                    topScore = entry.getValue().getDeathScore();
                    name = entry.getValue().getName();
                }
            }
            if (topPlayer == null) break;
            sortedPlayers.put(topPlayer, leaderboardTracker.get(topPlayer));
            if (skillName.equalsIgnoreCase("deaths"))
                sorted.add(ChatColor.AQUA.toString() + i + ". " + ChatColor.GOLD + name + ChatColor.AQUA + " - " + ChatColor.GREEN + ((int) topScore));
            else
                sorted.add(ChatColor.AQUA.toString() + i + ". " + ChatColor.GOLD + name + ChatColor.AQUA + " - " + ChatColor.GREEN + topScore);
        }
        return sorted;
    }

    public LeaderboardPlayer createLeaderboardPlayer(Player p) {
        int score = getLeaderboardScore(p, "All");
        int buildingScore = getLeaderboardScore(p, "Building");
        int craftingScore = getLeaderboardScore(p, "Crafting");
        int exploringScore = getLeaderboardScore(p, "Exploring");
        int farmingScore = getLeaderboardScore(p, "Farming");
        int fightingScore = getLeaderboardScore(p, "Fighting");
        int fishingScore = getLeaderboardScore(p, "Fishing");
        int miningScore = getLeaderboardScore(p, "Mining");
        int mainScore = getLeaderboardScore(p, "Main");
        int deathScore = getLeaderboardScore(p, "Deaths");

        return new LeaderboardPlayer(p.getDisplayName(), score, buildingScore, craftingScore, exploringScore,
                farmingScore, fightingScore, fishingScore, miningScore, mainScore, deathScore);
    }

    public void printRank(Player p, String skillName) {
        int rank = 1;
        for (Map.Entry<UUID, LeaderboardPlayer> entry : leaderboardTracker.entrySet()) {
            if (entry.getKey().equals(p.getUniqueId())) continue;
            if (skillName.equalsIgnoreCase("all") && entry.getValue().getScore() > getLeaderboardScore(p, "All")) rank++;
            else if (skillName.equalsIgnoreCase("building") && entry.getValue().getBuildingScore() > getLeaderboardScore(p, "Building")) rank++;
            else if (skillName.equalsIgnoreCase("crafting") && entry.getValue().getCraftingScore() > getLeaderboardScore(p, "Crafting")) rank++;
            else if (skillName.equalsIgnoreCase("exploring") && entry.getValue().getExploringScore() > getLeaderboardScore(p, "Exploring")) rank++;
            else if (skillName.equalsIgnoreCase("farming") && entry.getValue().getFarmingScore() > getLeaderboardScore(p, "Farming")) rank++;
            else if (skillName.equalsIgnoreCase("fighting") && entry.getValue().getFightingScore() > getLeaderboardScore(p, "Fighting")) rank++;
            else if (skillName.equalsIgnoreCase("fishing") && entry.getValue().getFishingScore() > getLeaderboardScore(p, "Fishing")) rank++;
            else if (skillName.equalsIgnoreCase("mining") && entry.getValue().getMiningScore() > getLeaderboardScore(p, "Mining")) rank++;
            else if (skillName.equalsIgnoreCase("main") && entry.getValue().getMainScore() > getLeaderboardScore(p, "Main")) rank++;
        }
        p.sendRawMessage(ChatColor.GREEN + "You are currently ranked " + ChatColor.GOLD + rank + ChatColor.GREEN + " in "
                + ChatColor.GOLD + skillName + ChatColor.GREEN + " out of " + ChatColor.GOLD + leaderboardTracker.size() + ChatColor.GREEN + "!");
    }

    public int getLeaderboardScore(Player p, String skillName) {
        if (skillName.equalsIgnoreCase("All")) {
            int score = 0;
            if (!playerSkills.containsKey(p.getUniqueId())) return score;
            for (Skill skill : playerSkills.get(p.getUniqueId())) score += skill.getLevel();
            return score;
        }
        else if (skillName.equals("Building")) {
            int score = 0;
            if (!playerSkills.containsKey(p.getUniqueId())) return score;
            return getSkill(p.getUniqueId(), "Building").getLevel();
        }
        else if (skillName.equals("Crafting")) {
            int score = 0;
            if (!playerSkills.containsKey(p.getUniqueId())) return score;
            return getSkill(p.getUniqueId(), "Crafting").getLevel();
        }
        else if (skillName.equals("Exploring")) {
            int score = 0;
            if (!playerSkills.containsKey(p.getUniqueId())) return score;
            return getSkill(p.getUniqueId(), "Exploring").getLevel();
        }
        else if (skillName.equals("Farming")) {
            int score = 0;
            if (!playerSkills.containsKey(p.getUniqueId())) return score;
            return getSkill(p.getUniqueId(), "Farming").getLevel();
        }
        else if (skillName.equals("Mining")) {
            int score = 0;
            if (!playerSkills.containsKey(p.getUniqueId())) return score;
            return getSkill(p.getUniqueId(), "Mining").getLevel();
        }
        else if (skillName.equals("Fighting")) {
            int score = 0;
            if (!playerSkills.containsKey(p.getUniqueId())) return score;
            return getSkill(p.getUniqueId(), "Fighting").getLevel();
        }
        else if (skillName.equals("Fishing")) {
            int score = 0;
            if (!playerSkills.containsKey(p.getUniqueId())) return score;
            return getSkill(p.getUniqueId(), "Fishing").getLevel();
        }
        else if (skillName.equals("Main")) {
            int score = 0;
            if (!playerSkills.containsKey(p.getUniqueId())) return score;
            return getSkill(p.getUniqueId(), "Main").getLevel();
        }
        else if (skillName.equalsIgnoreCase("Deaths")) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            if (manager == null) return 0;
            Scoreboard mainBoard = manager.getMainScoreboard();
            Objective objective = mainBoard.getObjective("deaths");
            if (objective == null) return 0;
            Score score = objective.getScore(p.getName());
            return score.getScore();
        }

        return 0;
    }

    public void leaderboardJoin(Player p) {
        leaderboardTracker.put(p.getUniqueId(), createLeaderboardPlayer(p));
        int deaths = getLeaderboardScore(p, "Deaths");
        if (deaths >= 40)
            p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false, true));
        if (deaths >= 50) {
            PlayerRewards rewards = getPlayerRewards(p);
            rewards.setProtectionPercentage(rewards.getProtectionPercentage() + 0.1);
            rewards.setAddedDeathResistance(true);
        }

        updateScoreboard(p, "Main");
    }

    public void runSkillAutoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            savePlayerData();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.runTask(SurvivalSkills.this);
            }
        }.runTaskTimerAsynchronously(this, 900, 900);
    }

    public void createTrails() {
        trails.put("Dust", Particle.DUST);
        trails.put("Water", Particle.SPLASH);
        trails.put("Happy", Particle.HAPPY_VILLAGER);
        trails.put("Dragon", Particle.DRAGON_BREATH);
        trails.put("Electric", Particle.ELECTRIC_SPARK);
        trails.put("Enchantment", Particle.ENCHANT);
        trails.put("Ominous", Particle.OMINOUS_SPAWNING);
        trails.put("Love", Particle.HEART);
        trails.put("Flame", Particle.FLAME);
        trails.put("BlueFlame", Particle.SOUL_FIRE_FLAME);
        trails.put("Cherry", Particle.CHERRY_LEAVES);
        trails.put("Rainbow", Particle.DUST);
    }

    public Enchantment getEnchantFromKey(String key) {
        for (Enchantment enchant : Registry.ENCHANTMENT) {
            if (enchant.getKey().toString().equalsIgnoreCase(key)) return enchant;
        }
        return Enchantment.EFFICIENCY;
    }

    public PlayerRewards getDefaultPlayerRewards() {
        return playerRewards;
    }

    public MiningSkill getMiningListener() {
        return miningListener;
    }

    public FarmingSkill getFarmingListener() {
        return farmingListener;
    }

    public BuildingSkill getBuildingListener() {
        return buildingListener;
    }

    public FishingSkill getFishingListener() {
        return fishingListener;
    }

    public FightingSkill getFightingListener() {
        return fightingListener;
    }

    public CraftingSkill getCraftingListener() {
        return craftingListener;
    }

    public ExploringSkill getExploringListener() {
        return exploringListener;
    }

    public MainSkill getMainListener() {
        return mainListener;
    }

    public PlayerListener getPlayerListener() {
        return playerListener;
    }

    public boolean isForced(Player p, String[] args) {
        if (!p.hasPermission("survivalskills.op")) return false;
        if (args.length < 1) return false;
        for (String arg : args) if (arg.equalsIgnoreCase("force")) return true;
        return false;
    }

    public FileConfiguration getTrueConfig() {
        return config;
    }

    public HashMap<UUID, HashMap<String, Boolean>> getTrophyTracker() {
        return trophyTracker;
    }

    public ArrayList<NamespacedKey> getRecipeKeys() {
        return recipeKeys;
    }

    public HashMap<Player, TrailEffect> getTrailTracker() {
        return trailTracker;
    }

    public File getDataFile() {
        return dataFile;
    }

    public FileConfiguration getData() {
        return data;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public boolean isGriefPreventionEnabled() {
        return griefPreventionEnabled;
    }

    public HashMap<UUID, LeaderboardPlayer> getLeaderboardTracker() {
        return leaderboardTracker;
    }

    public boolean isWoolRecipes() {
        return woolRecipes;
    }

    public void setWoolRecipes(boolean woolRecipes) {
        this.woolRecipes = woolRecipes;
    }

    public HashMap<String, Particle> getTrails() {
        return trails;
    }
}
