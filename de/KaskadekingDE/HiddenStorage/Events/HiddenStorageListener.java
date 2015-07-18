package de.KaskadekingDE.HiddenStorage.Events;

import de.KaskadekingDE.HiddenStorage.Classes.BlockHolder;
import de.KaskadekingDE.HiddenStorage.Classes.Helper;
import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.HiddenStorage;
import de.KaskadekingDE.HiddenStorage.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
                        p.sendMessage("§a[§bHiddenStorage§a] §cThis block is already a hidden storage!");
                        PlayersInQueue.remove(p);
                        e.setCancelled(true);
                        return;
                    }
                    BlockHolder bh = new BlockHolder("Hidden Storage", 54, b);
                    Inventory inv = bh.getInventory();
                    HiddenStorage hs = new HiddenStorage(p, loc, inv);
                    hs.SaveHiddenStorage();
                    p.sendMessage("§a[§bHiddenStorage§a] §eBlock at " + loc + " is now a hidden storage!");
                    PlayersInQueue.remove(p);
                    e.setCancelled(true);
                }
            }
        } else {
            if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location loc = e.getClickedBlock().getLocation();
                HiddenStorage hs = HiddenStorage.HSByLocation(loc);
                if(hs == null) return;
                e.setCancelled(true);
                if(!hs.EqualsOwner(p)) {
                    if(Main.PreventOthers) {
                        e.setCancelled(true);
                        return;
                    } else {
                        p.sendMessage("§a[§bHiddenStorage§a] §eYou found a hidden storage from " + hs.Owner.getName());
                        if(Main.NotifyOnOpen && hs.Owner.isOnline()) {
                            hs.Owner.getPlayer().sendMessage("§a[§bHiddenStorage§a] §cSomebody has opened your hidden storage!");
                        }
                    }
                }
                p.openInventory(hs.StorageInventory);
            }
        }

    }

    private boolean checkRequirements(Player p, Location loc) {
        // TODO Add some requirements
        return true;
    }

}
