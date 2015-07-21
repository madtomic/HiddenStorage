package de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.ChestManager;

import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.HiddenStorage;
import de.KaskadekingDE.HiddenStorage.Main;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HiddenStorageManager {
    public static List<HiddenStorage> hiddenStorageList = new ArrayList<HiddenStorage>();

    public static void Add(HiddenStorage hs) {
        hiddenStorageList.add(hs);
    }

    public static void Remove(HiddenStorage hs) {
        hiddenStorageList.remove(hs);
    }

    public static void Remove(Location loc) {
        HiddenStorage hs = GetByLocation(loc, null, true);
        if(hs != null) {
            hiddenStorageList.remove(hs);
        }
    }

    public static List<HiddenStorage> GetByOwner(OfflinePlayer p) {
        List<HiddenStorage> result = new ArrayList<HiddenStorage>();
        for(HiddenStorage hs: hiddenStorageList) {
            if(hs == null) {
                hiddenStorageList.removeAll(Collections.singleton(null));
                continue;
            } else if(hs.Owner == null) {
                hiddenStorageList.remove(hs);
                continue;
            }
            if(hs.Owner.getUniqueId().equals(p.getUniqueId())) {
                result.add(hs);
            }
        }
        return result;
    }

    /* @deprecated */
    @Deprecated
    public static HiddenStorage GetByLocation(Location loc, Player owner) {
        List<HiddenStorage> result = new ArrayList<HiddenStorage>();
        for(HiddenStorage hs: hiddenStorageList) {
            if(hs == null || hs.StorageLocation == null)
                continue;
            if(hs.StorageLocation.equals(loc))
                result.add(hs);
        }
        if(result.size() == 0) return null;
        if(result.size() == 1 || owner == null) return result.get(0);
        for(HiddenStorage temp: result) {
            if(temp.EqualsOwner(owner)) {
                return temp;
            }
        }
        return result.get(0);
    }

    public static HiddenStorage GetByLocation(Location loc, Player owner, boolean returnNullWhenNotFound) {
        List<HiddenStorage> result = new ArrayList<HiddenStorage>();
        for(HiddenStorage hs: hiddenStorageList) {
            if(hs == null || hs.StorageLocation == null)
                continue;
            if(hs.StorageLocation.equals(loc))
                result.add(hs);
        }
        if(result.size() == 0) return null;
        if(result.size() == 1 || owner == null) return result.get(0);
        for(HiddenStorage temp: result) {
            if(temp.EqualsOwner(owner)) {
                return temp;
            }
        }
        if(returnNullWhenNotFound) return null;
        return result.get(0);
    }

    public static List<HiddenStorage> GetByLocationList(Location loc, Player owner) {
        List<HiddenStorage> result = new ArrayList<HiddenStorage>();
        for(HiddenStorage hs: hiddenStorageList) {
            if(hs == null || hs.StorageLocation == null)
                continue;
            if(hs.StorageLocation.equals(loc))
                result.add(hs);
        }
        return result;
    }

    public static List<HiddenStorage> GetUsedBoughtStorages(Player owner) {
        List<HiddenStorage> result = new ArrayList<HiddenStorage>();
        for(HiddenStorage hs: hiddenStorageList) {
            if(hs == null || hs.StorageLocation == null)
                continue;
            if(hs.EqualsOwner(owner) && hs.BoughtChest)
                result.add(hs);
        }
        return result;
    }

    public static int TotalBoughtStorages(Player p) {
        return Main.HiddenStorageConfiguration.playerData.getPlayerConfig().getInt("players." + p.getUniqueId() + ".bought-chests", 0);
    }
}
