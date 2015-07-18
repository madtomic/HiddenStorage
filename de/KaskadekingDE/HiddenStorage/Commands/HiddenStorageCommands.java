package de.KaskadekingDE.HiddenStorage.Commands;

import de.KaskadekingDE.HiddenStorage.Classes.HiddenStorage.HiddenStorage;
import de.KaskadekingDE.HiddenStorage.Events.HiddenStorageListener;
import de.KaskadekingDE.HiddenStorage.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HiddenStorageCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            cs.sendMessage("§a[§bHiddenStorage§a] §eHiddenStorage v" + Main.plugin.getDescription().getVersion() + " by KaskadekingDE");
            cs.sendMessage("§eSee \"/" + label + " help\" for help");
            return true;
        }
        if(args[0].equalsIgnoreCase("help")) {
            if(!cs.hasPermission("hiddenstorage.help")) {
                cs.sendMessage("§a[§bHiddenStorage§a] §cYou don't have enough permissions for this command!");
                return true;
            }
            cs.sendMessage("§a[§bHiddenStorage§a] §eHelp Page 1/1:");
            cs.sendMessage("§6/" + label + " help §7- §eShows the help page");
            cs.sendMessage("§6/" + label + " set  §7- §eMake a block to a hidden storage");
            return true;
        } else if(args[0].equalsIgnoreCase("set")) {
           if(!(cs instanceof Player)) {
               cs.sendMessage("§a[§bHiddenStorage§a] §cYou can only execute this command as a player!");
               return true;
           }
            Player p = (Player) cs;
            if(!p.hasPermission("hiddenstorage.set")) {
                p.sendMessage("§a[§bHiddenStorage§a] §cYou don't have enough permissions for this command!");
                return true;
            }
            if(HiddenStorage.NextAvailableId(p) == -1) {
                p.sendMessage("§a[§bHiddenStorage§a] §cYou cannot set more than " + Main.MaximumHiddenStorages + " hidden storages!");
                return true;
            }
            HiddenStorageListener.PlayersInQueue.add(p);
            p.sendMessage("§a[§bHiddenStorage§a] §eRightclick on a block to make it a hidden storage. Leftclick to cancel");
            return true;
        }
        return false;
    }
}
