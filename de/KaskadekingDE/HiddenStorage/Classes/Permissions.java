package de.KaskadekingDE.HiddenStorage.Classes;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class Permissions {
    public static boolean hasPermission(Player p, String node) {
        if(p.hasPermission(node) || p.hasPermission("*"))
            return true;
        for(int y = 0; y < node.length(); y++)
            if(node.charAt(y) == '.')
                if(p.hasPermission(node.substring(0, y) + ".*"))
                    return true;
        return false;
    }

    public static boolean hasPermission(CommandSender cs, String node) {
        if(cs.hasPermission(node) || cs.hasPermission("*"))
            return true;
        for(int y = 0; y < node.length(); y++)
            if(node.charAt(y) == '.')
                if(cs.hasPermission(node.substring(0, y) + ".*"))
                    return true;
        return false;
    }

    public static int getPermissionNumber(Player p, String node) {
        if(!node.endsWith(".")) node = node + ".";
        for(PermissionAttachmentInfo perm: p.getEffectivePermissions()) {
            String permName = perm.getPermission();
            if(permName.startsWith(node)) {
                String[] subNodes = permName.split("\\.");
                String sNumber = subNodes[subNodes.length - 1];
                if(!Helper.IsNumeric(sNumber)) return -1;
                return Integer.parseInt(sNumber);
            }
        }
        return -1;
    }
}
