package de.KaskadekingDE.HiddenStorage;

import de.KaskadekingDE.HiddenStorage.Classes.BlockHolder;
import de.KaskadekingDE.HiddenStorage.Classes.Configuration;
import de.KaskadekingDE.HiddenStorage.Classes.Helper;
import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.ChestManager.HiddenStorageManager;
import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.HiddenStorage;
import de.KaskadekingDE.HiddenStorage.Classes.ProtectedRegionManager;
import de.KaskadekingDE.HiddenStorage.Classes.Serialization.InventorySerialization;
import de.KaskadekingDE.HiddenStorage.Commands.HiddenStorageCommands;
import de.KaskadekingDE.HiddenStorage.Config.PlayerData;
import de.KaskadekingDE.HiddenStorage.Events.ChestProtector;
import de.KaskadekingDE.HiddenStorage.Events.HiddenStorageListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft");
    public static Main plugin;
    public static Configuration HiddenStorageConfiguration;
    public static ProtectedRegionManager RegionManager;
    public static Economy Economy = null;

    @Override
    public void onEnable() {
        plugin = this;
        HiddenStorageConfiguration = new Configuration(this);
        HiddenStorageConfiguration.LoadConfig();
        RegionManager = new ProtectedRegionManager();
        CheckVault();
        getCommand("hiddenstorage").setExecutor(new HiddenStorageCommands());
        Bukkit.getPluginManager().registerEvents(new HiddenStorageListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestProtector(), this);
        PluginDescriptionFile pdf = getDescription();
        log.info("[HiddenStorage] HiddenStorage v" + pdf.getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        plugin = null;
        log.info("[HiddenStorage] HiddenStorage has been disabled!");
        HiddenStorageConfiguration.SaveConfig();
    }

     public static void CheckVault() {
        if(HiddenStorageConfiguration.UseVault) {
            Plugin p = Bukkit.getPluginManager().getPlugin("Vault");
            if(p == null || !p.isEnabled()) {
                log.severe("[HiddenStorage] Vault support was enabled in the config but it isn't installed!");
                HiddenStorageConfiguration.UseVault = false;
            } else {
                RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                if(economyProvider != null) {
                    Economy = economyProvider.getProvider();
                }
                if(Economy == null) {
                    log.severe("[HiddenStorage] Failed to get the Economy Provider from Vault!");
                    HiddenStorageConfiguration.UseVault = false;
                }
            }
        }
    }
}
