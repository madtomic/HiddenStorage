package de.KaskadekingDE.HiddenStorage.Events;

import com.intellectualcrafters.plot.commands.Help;
import de.KaskadekingDE.HiddenStorage.Classes.BlockHolder;
import de.KaskadekingDE.HiddenStorage.Classes.Helper;
import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.ChestManager.HiddenStorageManager;
import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.HiddenStorage;
import de.KaskadekingDE.HiddenStorage.Classes.Permissions;
import de.KaskadekingDE.HiddenStorage.Main;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class HiddenStorageListener implements Listener {

    public static List<Player> PlayersInQueue = new ArrayList<Player>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(PlayersInQueue.contains(e.getPlayer())) {
            if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block b = e.getClickedBlock();
                Location oldLoc = b.getLocation();
                Location loc = new Location(oldLoc.getWorld(), oldLoc.getBlockX(), oldLoc.getBlockY(), oldLoc.getBlockZ());
                if(checkRequirements(p, loc)) {
                    if(Helper.GetChestType(loc) != Helper.ChestState.Default) {
                        HiddenStorage temp = HiddenStorage.StorageByLocation(loc, p, true);
                        if(temp != null && temp.Owner.equals(p) || temp != null && Main.HiddenStorageConfiguration.BlockMoreThanOnce) {
                            p.sendMessage("§a[§bHiddenStorage§a] §cThis block is already a hidden storage!");
                            PlayersInQueue.remove(p);
                            e.setCancelled(true);
                            return;
                        }
                    }
                    if(Main.HiddenStorageConfiguration.UseVault && HiddenStorageManager.GetUsedBoughtStorages(p).size() >= HiddenStorageManager.TotalBoughtStorages(p)) {
                        EconomyResponse r = Main.Economy.withdrawPlayer(p, Helper.GetPrice(p));
                        String price = Main.Economy.format(Helper.GetPrice(p));
                        if(r.transactionSuccess()) {
                            p.sendMessage("§a[§bHiddenStorage§a] §e" + price + " has been taken from your account!");
                            HiddenStorage.BuyStorage(p);
                        } else {
                            p.sendMessage("§a[§bHiddenStorage§a] §cFailed to take " + price + " from your account! Error: " + r.errorMessage);
                            PlayersInQueue.remove(p);
                            e.setCancelled(true);
                            return;
                        }
                    }
                    BlockHolder bh = new BlockHolder("Hidden Storage", 54, b);
                    Inventory inv = bh.getInventory();
                    HiddenStorage hs = new HiddenStorage(p, loc, inv, Main.HiddenStorageConfiguration.UseVault);
                    hs.SaveHiddenStorage();
                    p.sendMessage("§a[§bHiddenStorage§a] §eBlock at " + Helper.LocationToString(loc, false) + " is now a hidden storage!");
                    PlayersInQueue.remove(p);
                    if(Main.HiddenStorageConfiguration.OpenOnSet)
                        onPlayerInteract(e);
                    e.setCancelled(true);
                }
            } else if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                p.sendMessage("§a[§bHiddenStorage§a] §eThe current operation has been cancelled!");
                PlayersInQueue.remove(p);
                e.setCancelled(true);
            }
        } else {
            if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location loc = e.getClickedBlock().getLocation();
                HiddenStorage hs = HiddenStorage.StorageByLocation(loc, p, true);
                if(hs == null) return;
                if(p.isSneaking() && !Main.HiddenStorageConfiguration.OpenOnSneak)
                    return;
                e.setCancelled(true);
                if(!hs.EqualsOwner(p)) {
                    if(Main.HiddenStorageConfiguration.PreventOthers && !Permissions.hasPermission(p, "hiddenstorage.protection.bypass")) {
                        return;
                    } else {
                        p.sendMessage("§a[§bHiddenStorage§a] §eYou found a hidden storage from " + hs.Owner.getName());
                        if(Main.HiddenStorageConfiguration.NotifyOnOpen && hs.Owner.isOnline()) {
                            hs.Owner.getPlayer().sendMessage("§a[§bHiddenStorage§a] §cSomebody has opened your hidden storage!");
                        }
                    }
                }
                p.openInventory(hs.StorageInventory);
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!Main.HiddenStorageConfiguration.RemoveWhenEmpty) return;
        if(e.getInventory().getHolder() instanceof BlockHolder) {
            BlockHolder bh = (BlockHolder) e.getInventory().getHolder();
            Player p = (Player) e.getPlayer();
            HiddenStorage hs = HiddenStorage.StorageByLocation(bh.getBlock().getLocation(), p, false);
            if(Helper.InventoryEmpty(e.getInventory())) {
                if(!hs.EqualsOwner(p)) {
                    p.sendMessage("§a[§bHiddenStorage§a] §eThe hidden storage of " + hs.Owner.getName() + " has been removed because it's empty.");
                } else {
                    p.sendMessage("§a[§bHiddenStorage§a] §eYour hidden storage has been removed because it's empty.");
                }
                hs.RemoveChest();
            }
        }
    }

    private boolean checkRequirements(Player p, Location loc) {
        if(Main.HiddenStorageConfiguration.RevertWhitelist) {
            if(Main.HiddenStorageConfiguration.AllowedWorlds.contains(loc.getWorld().getName()) || Main.HiddenStorageConfiguration.AllowedWorlds.contains("*")) {
                p.sendMessage("§a[§bHiddenStorage§a] §cYou're not allowed to set a hidden storage in this world!");
                return false;
            }
        } else {
            if(!Main.HiddenStorageConfiguration.AllowedWorlds.contains(loc.getWorld().getName()) && !Main.HiddenStorageConfiguration.AllowedWorlds.contains("*")) {
                p.sendMessage("§a[§bHiddenStorage§a] §cYou're not allowed to set a hidden storage in this world!");
                return false;
            }
        }
        if(!Main.RegionManager.checkAccess(p, loc, loc.getBlock().getType())) {
            p.sendMessage("§a[§bHiddenStorage§a] §eThis region is protected! You can't set here a hidden storage.");
            return false;
        }
        if(Main.HiddenStorageConfiguration.UseVault && HiddenStorageManager.GetUsedBoughtStorages(p).size() >= HiddenStorageManager.TotalBoughtStorages(p) && Main.Economy.getBalance(p) < Helper.GetPrice(p)) {
            String price = Main.Economy.format(Main.HiddenStorageConfiguration.Price);
            String balance = Main.Economy.format(Main.Economy.getBalance(p));
            p.sendMessage("§a[§bHiddenStorage§a] §cA hidden storage costs " + price + ". You only have " + balance);
            return false;
        }
        return true;
    }

}
