package de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage;

import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.ChestManager.HiddenStorageManager;
import de.KaskadekingDE.HiddenStorage.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class HiddenStorage implements Comparable<HiddenStorage> {

    public Inventory StorageInventory;
    public Location StorageLocation;
    public OfflinePlayer Owner;

    public HiddenStorage(Player owner, Location loc, Inventory inv) {
        Owner = owner;
        StorageInventory = inv;
        StorageLocation = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        HiddenStorageManager.Add(this);
    }

    public HiddenStorage(OfflinePlayer owner, Location loc, Inventory inv) {
        Owner = owner;
        StorageInventory = inv;
        StorageLocation = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        HiddenStorageManager.Add(this);
    }

    public static void CreateHiddenStorage(OfflinePlayer owner, Location loc, Inventory inv) { new HiddenStorage(owner, loc, inv); }

    public void SaveHiddenStorage() {
        int id = GetId();
        if(id == -1) {
            id = NextAvailableId(Owner);
        }
        String base = Main.Serialization.Serialize(StorageInventory);
        Main.playerData.getPlayerConfig().set("players." + Owner.getUniqueId() + ".name", Owner.getName());
        Main.playerData.getPlayerConfig().set("players." + Owner.getUniqueId() + ".hidden-storages." + id + ".x", StorageLocation.getBlockX());
        Main.playerData.getPlayerConfig().set("players." + Owner.getUniqueId() + ".hidden-storages." + id + ".y", StorageLocation.getBlockY());
        Main.playerData.getPlayerConfig().set("players." + Owner.getUniqueId() + ".hidden-storages." + id + ".z", StorageLocation.getBlockZ());
        Main.playerData.getPlayerConfig().set("players." + Owner.getUniqueId() + ".hidden-storages." + id + ".world", StorageLocation.getWorld().getName());
        Main.playerData.getPlayerConfig().set("players." + Owner.getUniqueId() + ".hidden-storages." + id + ".inventory", base);
        Main.playerData.savePlayerConfig();
    }

    public void RemoveChest() {
        if(Owner == null || StorageInventory == null || StorageLocation == null) {
            return;
        }
        Main.playerData.getPlayerConfig().set("players." + Owner.getUniqueId() + ".hidden-storages." + GetId(), null);
        Main.playerData.savePlayerConfig();
        HiddenStorageManager.Remove(StorageLocation);
        Owner = null;
        StorageInventory = null;
        StorageLocation = null;
    }

    public static HiddenStorage HSByPlayer(OfflinePlayer p) {
        return HiddenStorageManager.GetByOwner(p);
    }

    public static HiddenStorage HSByLocation(Location loc) {
        return HiddenStorageManager.GetByLocation(loc);
    }

    public int GetId() {
        if(Main.playerData.getPlayerConfig().contains("players." + Owner.getUniqueId()) && Main.playerData.getPlayerConfig().contains("players." + Owner.getUniqueId() + ".hidden-storages")) {
            for(int i = 1; i <= Main.MaximumHiddenStorages; i++) {
                if(Main.playerData.getPlayerConfig().contains("players." + Owner.getUniqueId() + ".hidden-storages." + i)) {
                    int x;
                    int y;
                    int z;
                    String world;
                    x = Main.playerData.getPlayerConfig().getInt("players." + Owner.getUniqueId() + ".hidden-storages." + i + ".x", 0);
                    y = Main.playerData.getPlayerConfig().getInt("players." + Owner.getUniqueId() + ".hidden-storages." + i + ".y", -1);
                    z = Main.playerData.getPlayerConfig().getInt("players." + Owner.getUniqueId() + ".hidden-storages." + i + ".z", 0);
                    world = Main.playerData.getPlayerConfig().getString("players." + Owner.getUniqueId() + ".hidden-storages." + i + ".world", null);
                    if(world != null) {
                        World w = Bukkit.getWorld(world);
                        if(w != null) {
                            Location loc = new Location(w, x, y, z);
                            if(loc.equals(StorageLocation)) {
                                return i;
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    public static int NextAvailableId(OfflinePlayer p) {
        for(int i = 1; i <= Main.MaximumHiddenStorages; i++) {
            if(!Main.playerData.getPlayerConfig().contains("players." + p.getUniqueId() + ".hidden-storages." + i)) {;
                return i;
            }
        }
        return -1;
    }

    public boolean EqualsOwner(Player p) {
        return Owner.getUniqueId().equals(p.getUniqueId());
    }

    @Override
    public int compareTo(HiddenStorage otherHs) {
        String id = Integer.toString(GetId());
        String id2 = Integer.toString(otherHs.GetId());
        return id.compareTo(id2);
    }
}
