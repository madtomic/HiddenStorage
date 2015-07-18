package de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.ChestManager;

import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.HiddenStorage;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
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
        HiddenStorage hs = GetByLocation(loc);
        if(hs != null) {
            hiddenStorageList.remove(hs);
        }
    }

    public static HiddenStorage GetByOwner(OfflinePlayer p) {
        for(HiddenStorage hs: hiddenStorageList) {
            if(hs.Owner.getUniqueId().equals(p.getUniqueId()))
                return hs;
        }
        return null;
    }

    public static HiddenStorage GetByLocation(Location loc) {
        for(HiddenStorage hs: hiddenStorageList) {
            if(hs == null || hs.StorageLocation == null)
                continue;
            if(hs.StorageLocation.equals(loc))
                return hs;
        }
        return null;
    }
}
