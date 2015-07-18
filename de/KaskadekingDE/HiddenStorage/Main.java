package de.KaskadekingDE.HiddenStorage;

import de.KaskadekingDE.HiddenStorage.Classes.BlockHolder;
import de.KaskadekingDE.HiddenStorage.Classes.Helper;
import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.ChestManager.HiddenStorageManager;
import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.HiddenStorage;
import de.KaskadekingDE.HiddenStorage.Classes.Serialization.InventorySerialization;
import de.KaskadekingDE.HiddenStorage.Commands.HiddenStorageCommands;
import de.KaskadekingDE.HiddenStorage.Config.PlayerData;
import de.KaskadekingDE.HiddenStorage.Events.ChestProtector;
import de.KaskadekingDE.HiddenStorage.Events.HiddenStorageListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft");
    public static Main plugin;
    public static InventorySerialization Serialization;

    public static PlayerData playerData;

    public static int MaximumHiddenStorages;
    public static boolean ProtectFromExplosions;
    public static boolean NotifyUser;
    public static boolean PreventOthers;
    public static boolean AllowBreakingFromOthers;
    public static boolean NotifyOnOpen;

    @Override
    public void onEnable() {
        plugin = this;
        Serialization = new InventorySerialization();
        LoadConfig();
        getCommand("hiddenstorage").setExecutor(new HiddenStorageCommands());
        Bukkit.getPluginManager().registerEvents(new HiddenStorageListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestProtector(), this);
        PluginDescriptionFile pdf = getDescription();
        log.info("[HiddenStorage] HiddenStorage v" + pdf.getVersion() + " has been enabled!");
    }

    public void LoadConfig() {
        getConfig().addDefault("maximum-number-of-hs", 5);
        getConfig().addDefault("protect-storages-from-explosions", true);
        getConfig().addDefault("notify-user-on-storage-destroy", true);
        getConfig().addDefault("notify-user-when-somebody-has-opened-hs", false);
        getConfig().addDefault("allow-other-players-to-break-hs", true);
        getConfig().addDefault("prevent-opening-from-other-players", false);
        getConfig().options().copyDefaults(true);
        saveConfig();
        MaximumHiddenStorages = getConfig().getInt("maximum-number-of-hs");
        ProtectFromExplosions = getConfig().getBoolean("protect-storages-from-explosions");
        NotifyUser = getConfig().getBoolean("notify-user-on-storage-destroy");
        AllowBreakingFromOthers = getConfig().getBoolean("allow-other-players-to-break-hs");
        PreventOthers = getConfig().getBoolean("prevent-opening-from-other-players");
        NotifyOnOpen = getConfig().getBoolean("notify-user-when-somebody-has-opened-hs");
        playerData = new PlayerData(this);
        playerData.saveDefaultPlayerConfig();
        playerData.reloadPlayerConfig();

        if(playerData.getPlayerConfig().contains("players")) {
            for(String uuidKey: playerData.getPlayerConfig().getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidKey);
                OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                if(playerData.getPlayerConfig().contains("players." + p.getUniqueId() + ".hidden-storages")) {
                    if(playerData.getPlayerConfig().getConfigurationSection("players." + p.getUniqueId() + ".hidden-storages") == null) continue;
                    for(String id: playerData.getPlayerConfig().getConfigurationSection("players." + p.getUniqueId() + ".hidden-storages").getKeys(false)) {
                        int x;
                        int y;
                        int z;
                        String world;
                        String base;
                        x = Main.playerData.getPlayerConfig().getInt("players." + p.getUniqueId() + ".hidden-storages." + id + ".x", 0);
                        y = Main.playerData.getPlayerConfig().getInt("players." + p.getUniqueId() + ".hidden-storages." + id + ".y", -1);
                        z = Main.playerData.getPlayerConfig().getInt("players." + p.getUniqueId() + ".hidden-storages." + id + ".z", 0);
                        world = Main.playerData.getPlayerConfig().getString("players." + p.getUniqueId() + ".hidden-storages." + id + ".world", null);
                        base = Main.playerData.getPlayerConfig().getString("players." + p.getUniqueId() + ".hidden-storages." + id + ".inventory", null);
                        World w;
                        if(world == null) {
                            w = null;
                        } else {
                            w = Bukkit.getWorld(world);
                        }
                        if(x == 0 && y == -1 && z == 0) {
                            Main.playerData.getPlayerConfig().set("players." + p.getUniqueId() + ".hidden-storages." + id, null);
                            continue;
                        }
                        if(w != null) {
                            Location loc = new Location(w, x, y, z);
                            Inventory inv;
                            Block b = loc.getBlock();
                            BlockHolder bh = new BlockHolder("Hidden Storage", 54, b);
                            inv = bh.getInventory();
                            if(base != null) {
                                Inventory baseInv = Serialization.Deserialize(base);
                                Helper.MoveItemsToInventory(baseInv, inv);
                            }
                            HiddenStorage hs = new HiddenStorage(p, loc, inv);
                        } else {
                            Main.playerData.getPlayerConfig().set("players." + p.getUniqueId() + ".hidden-storages." + id, null);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
        log.info("[HiddenStorage] HiddenStorage has been disabled!");
        SaveStorages();
    }

    public void SaveStorages() {
        for(HiddenStorage hs: HiddenStorageManager.hiddenStorageList) {
            hs.SaveHiddenStorage();
        }
        saveConfig();
        HiddenStorageManager.hiddenStorageList.clear();
    }
}
