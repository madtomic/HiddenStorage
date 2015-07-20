package de.KaskadekingDE.HiddenStorage.Commands;

import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.HiddenStorage;
import de.KaskadekingDE.HiddenStorage.Classes.Permissions;
import de.KaskadekingDE.HiddenStorage.Events.HiddenStorageListener;
import de.KaskadekingDE.HiddenStorage.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HiddenStorageCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            cs.sendMessage("§a[§bHiddenStorage§a] §eHiddenStorage v" + Main.plugin.getDescription().getVersion() + " by KaskadekingDE");
            cs.sendMessage("§eSee \"/" + label + " help\" for help");
            return true;
        }
        if(args[0].equalsIgnoreCase("help")) {
            if (!Permissions.hasPermission(cs, "hiddenstorage.help")) {
                cs.sendMessage("§a[§bHiddenStorage§a] §cYou don't have enough permissions for this command!");
                return true;
            }
            cs.sendMessage("§a[§bHiddenStorage§a] §eHelp Page 1/1:");
            cs.sendMessage("§6/" + label + " help §7- §eShows the help page");
            cs.sendMessage("§6/" + label + " reload §7- §eReloads the main config");
            cs.sendMessage("§6/" + label + " set  §7- §eMake a block to a hidden storage");
            cs.sendMessage("§6/" + label + " locations §7- §eGet the locations of your hidden storages");
            cs.sendMessage("§6/" + label + " locations [player] §7- §eGet the locations of hidden storages from a specifc player.");
            return true;
        } else if(args[0].equalsIgnoreCase("reload")) {
            if (!Permissions.hasPermission(cs, "hiddenstorage.reload")) {
                cs.sendMessage("§a[§bHiddenStorage§a] §cYou don't have enough permissions for this command!");
                return true;
            }
            Main.HiddenStorageConfiguration.SaveSubConfigs();
            Main.HiddenStorageConfiguration.LoadConfig();
            cs.sendMessage("§a[§bHiddenStorage§a] §eThe configuration has been reloaded!");
            return true;
        } else if(args[0].equalsIgnoreCase("set")) {
           if(!(cs instanceof Player)) {
               cs.sendMessage("§a[§bHiddenStorage§a] §cYou can only execute this command as a player!");
               return true;
           }
            Player p = (Player) cs;
            if(!Permissions.hasPermission(cs, "hiddenstorage.set")) {
                p.sendMessage("§a[§bHiddenStorage§a] §cYou don't have enough permissions for this command!");
                return true;
            }
            int maxStorages = Permissions.getPermissionNumber(p, "hiddenstorage.set.maximum");
            if(maxStorages == -1) {
                maxStorages = Main.HiddenStorageConfiguration.MaximumHiddenStorages;
            }
            if(HiddenStorage.NextAvailableId(p) == -1) {
                p.sendMessage("§a[§bHiddenStorage§a] §cYou cannot set more than " + maxStorages + " hidden storages!");
                return true;
            }
            HiddenStorageListener.PlayersInQueue.add(p);
            p.sendMessage("§a[§bHiddenStorage§a] §eRightclick on a block to make it a hidden storage. Leftclick to cancel");
            return true;
        } else if(args[0].equalsIgnoreCase("locations")) {
            if(args.length == 1) {
                if(!(cs instanceof Player)) {
                    cs.sendMessage("§a[§bHiddenStorage§a] §cYou can only execute this command as a player!");
                    return true;
                }
                if(!Permissions.hasPermission(cs, "hiddenstorage.locations")) {
                    cs.sendMessage("§a[§bHiddenStorage§a] §cYou don't have enough permissions for this command!");
                    return true;
                }
                Player p = (Player) cs;
                List<HiddenStorage> hiddenStorageList = HiddenStorage.HSByPlayer(p);
                Collections.sort(hiddenStorageList);
                if(hiddenStorageList.size() != 0) {
                    p.sendMessage("§a[§bHiddenStorage§a] §eList of all locations of your hidden storages: ");
                    for(HiddenStorage hs: hiddenStorageList) {
                        String id = Integer.toString(hs.GetId());
                        int x = hs.StorageLocation.getBlockX();
                        int y = hs.StorageLocation.getBlockY();
                        int z = hs.StorageLocation.getBlockZ();
                        World world = hs.StorageLocation.getWorld();
                        p.sendMessage("§6Hidden Storage Nr. \"" + id + "\": §aX: " + x + " Y: " + y + " Z: " + z + " World: " + world.getName());
                    }
                } else {
                    p.sendMessage("§a[§bHiddenStorage§a] §cYou don't have any hidden storages!");
                }
                return true;
            } else if(args.length == 2) {
                if(!Permissions.hasPermission(cs, "hiddenstorage.locations.others")) {
                    cs.sendMessage("§a[§bHiddenStorage§a] §cYou don't have enough permissions for this command!");
                    return true;
                }
                String name = args[1];
                if(cs instanceof Player && Objects.equals(cs.getName(), name) && !Permissions.hasPermission(cs, "hiddenstorage.locations")) {
                    cs.sendMessage("§a[§bHiddenStorage§a] §cYou don't have enough permissions to see the locations of your hidden storages!");
                    return true;
                }
                Player sender = (Player) cs;
                OfflinePlayer p = Bukkit.getOfflinePlayer(name);
                List<HiddenStorage> hiddenStorageList = HiddenStorage.HSByPlayer(p);
                Collections.sort(hiddenStorageList);
                if(hiddenStorageList.size() != 0) {
                    sender.sendMessage("§a[§bHiddenStorage§a] §eList of all locations of " + name + "'s hidden storages: ");
                    for(HiddenStorage hs: hiddenStorageList) {
                        String id = Integer.toString(hs.GetId());
                        int x = hs.StorageLocation.getBlockX();
                        int y = hs.StorageLocation.getBlockY();
                        int z = hs.StorageLocation.getBlockZ();
                        World world = hs.StorageLocation.getWorld();
                        sender.sendMessage("§6Hidden Storage Nr. \"" + id + "\": §aX: " + x + " Y: " + y + " Z: " + z + " World: " + world.getName());
                    }
                } else {
                    sender.sendMessage("§a[§bHiddenStorage§a] §c" + name + " don't have any hidden storages!");
                }
                return true;
            } else {
                cs.sendMessage("§a[§bHiddenStorage§a] §cToo many arguments. Correct usage: §e/" + label + " locations [player]");
            }
            return true;
        } else {
            cs.sendMessage("§a[§bHiddenStorage§a] §cUnknown command. Type §e\"/" + label + " help\" §cfor a list of all commands.");
            return true;
        }
    }
}
