package de.KaskadekingDE.HiddenStorage.Events;

import de.KaskadekingDE.HiddenStorage.Classes.Helper;
import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.HiddenStorage;
import de.KaskadekingDE.HiddenStorage.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class ChestProtector implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();
        Location loc = block.getLocation();
        Location loc2 = Helper.ChestNearLocation(loc);
        Helper.ChestState state = Helper.GetChestType(loc2);
        if(block.getType() == Material.CHEST) {
            if(state == Helper.ChestState.HiddenStorage)
                e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent e) {
        Block block = e.getBlock();
        Helper.ChestState state = Helper.GetChestType(block.getLocation());
        if(state == Helper.ChestState.HiddenStorage) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBurn(BlockBurnEvent e) {
        Block block = e.getBlock();
        Helper.ChestState state = Helper.GetChestType(block.getLocation());
        if(state == Helper.ChestState.HiddenStorage) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChangeBlock(EntityChangeBlockEvent e) {
        Block block = e.getBlock();
        Helper.ChestState state = Helper.GetChestType(block.getLocation());
        if(state == Helper.ChestState.HiddenStorage) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent e) {
        Iterator<Block> iter = e.blockList().iterator();
        Location blockUnderSign = null;
        Inventory inv = null;
        while(iter.hasNext()) {
            Block b = iter.next();
            HiddenStorage hs = HiddenStorage.HSByLocation(b.getLocation());
            if(hs != null && Main.ProtectFromExplosions) {
                iter.remove();
            } else if(hs != null && !Main.ProtectFromExplosions) {
                inv = hs.StorageInventory;
                if(hs.Owner.isOnline() && Main.NotifyUser) {
                    hs.Owner.getPlayer().sendMessage("§a[§bHiddenStorage§a] §cOne of your hidden storages has been destroyed!");
                }
                for(int i = 0; i < inv.getViewers().size(); i++) {
                    HumanEntity viewer = inv.getViewers().get(i);
                    viewer.closeInventory();
                }
                for (ItemStack i : hs.StorageInventory.getContents())
                {
                    if(i != null) {
                        hs.StorageLocation.getWorld().dropItemNaturally(hs.StorageLocation, i);
                    }
                }
                hs.RemoveChest();
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Location loc = block.getLocation();
        Helper.ChestState state = Helper.GetChestType(loc);
        if(state == Helper.ChestState.HiddenStorage) {
            HiddenStorage hs = HiddenStorage.HSByLocation(loc);
            if(!Main.AllowBreakingFromOthers && !hs.EqualsOwner(e.getPlayer())) {
                e.setCancelled(true);
                return;
            }
            if(!hs.EqualsOwner(e.getPlayer())) {
                e.getPlayer().sendMessage("§a[§bHiddenStorage§a] §eYou found a hidden storage from " + hs.Owner.getName());
            }
            for(HumanEntity viewer: hs.StorageInventory.getViewers()) {
                viewer.closeInventory();
            }
            for (ItemStack i : hs.StorageInventory.getContents())
            {
                if(i != null) {
                    hs.StorageLocation.getWorld().dropItemNaturally(hs.StorageLocation, i);
                }
            }
            if(Main.NotifyUser && hs.Owner.isOnline() && !hs.EqualsOwner(e.getPlayer())) {
                hs.Owner.getPlayer().sendMessage("§a[§bHiddenStorage§a] §cOne of your hidden storages has been destroyed!");
            }
            hs.RemoveChest();

        }
    }
}
