package de.KaskadekingDE.HiddenStorage.Classes;

import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.HiddenStorage;
import de.KaskadekingDE.HiddenStorage.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Helper {

    public enum ChestState {
        Default,
        HiddenStorage
    }

    public static ChestState GetChestType(Location loc) {
        if(HiddenStorage.StorageByLocation(loc, null, false) != null)
            return ChestState.HiddenStorage;
        return ChestState.Default;
    }

    public static boolean IsNumeric(String str) {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public static Inventory InventoryFromItemStack(ItemStack[] stack)
    {
        Inventory result = Bukkit.createInventory(null, 54);
        for(ItemStack item: stack) {
            if(item != null)
                result.addItem(item);
        }
        return result;
    }

    public static Location ChestNearLocation(Location loc) {
        if(loc.getBlock().getType() == Material.CHEST) {
            Location locEast = new Location(loc.getWorld(), loc.getX() + 1, loc.getY(), loc.getZ());
            Location locSouth = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + 1);
            Location locWest = new Location(loc.getWorld(), loc.getX() - 1, loc.getY(), loc.getZ());
            Location locNorth =  new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() - 1);
            if(locEast.getBlock().getType() == Material.CHEST) {
                return locEast;
            } else if(locSouth.getBlock().getType() == Material.CHEST) {
                return locSouth;
            } else if(locWest.getBlock().getType() == Material.CHEST){
                return locWest;
            } else if(locNorth.getBlock().getType() == Material.CHEST) {
                return locNorth;
            }
        }
        return null;
    }

    public static void MoveItemsToInventory(Inventory oldInv, Inventory newInv) {
        for(ItemStack item: oldInv.getContents()) {
            if(item != null) {
                newInv.addItem(item);
            }
        }
    }

    public static boolean InventoryEmpty(Inventory inv) {
        for(ItemStack is: inv.getContents()) {
            if(is != null)
                return false;
        }
        return true;
    }

    public static String LocationToString(Location loc, boolean appendWorld) {
        String result =  "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ();
        if(appendWorld)
            result += " World: " + loc.getWorld().getName();
        return result;
    }

    public static double GetPrice(Player p) {
        if(!Main.HiddenStorageConfiguration.UseVault) return 0;
        double price = Main.HiddenStorageConfiguration.Price;
        int discount = Permissions.getPermissionNumber(p, "hiddenstorage.set.discount");
        if(discount == -1) discount = 0;
        if(discount != 0) {
            int temp = 100 - discount;
            price = price * temp / 100.0;
        }
        return price;
    }
}
