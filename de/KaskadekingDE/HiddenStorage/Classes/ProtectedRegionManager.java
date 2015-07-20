package de.KaskadekingDE.HiddenStorage.Classes;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import de.KaskadekingDE.HiddenStorage.Main;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;

public class ProtectedRegionManager {

    public enum PlotSquaredType {
        NotLoaded,
        HasAccess,
        IsAdded,
        NoAccess,
    }

    public boolean HookedWorldGuard;
    public boolean HookedGriefPrevention;
    public boolean HookedTowny;
    public boolean HookedPlotSquared;

    public ProtectedRegionManager() {
        Initialize();
    }

    private boolean wgEnabled() {
        Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if(wg != null && wg.isEnabled()) {
            return true;
        }
        return false;
    }

    private boolean psEnabled() {
        Plugin ps = Bukkit.getPluginManager().getPlugin("PlotSquared");
        if(ps != null && ps.isEnabled()) {
            return true;
        }
        return false;
    }

    private boolean gpEnabled() {
        Plugin gp = Bukkit.getPluginManager().getPlugin("GriefPrevention");
        if(gp != null && gp.isEnabled()) {
            return true;
        }
        return false;
    }

    private boolean townyEnabled() {
        Plugin towny = Bukkit.getPluginManager().getPlugin("Towny");
        if(towny != null && towny.isEnabled()) {
            return true;
        }
        return false;
    }

    private void Initialize() {
        HookedWorldGuard = wgEnabled();
        HookedGriefPrevention = gpEnabled();
        HookedTowny = townyEnabled();
        HookedPlotSquared = psEnabled();
    }

    public boolean wgRegionAccess(Player p, Location loc, Material type) {
        if(!HookedWorldGuard || !wgEnabled())
            return true;
        if(!WGBukkit.getPlugin().createProtectionQuery().testBlockPlace(p, loc, type)) {
            return false;
        }
        return true;
    }

    public PlotSquaredType psPlotAccess(Player p, Location loc) {
        if(!HookedPlotSquared || !psEnabled())
            return PlotSquaredType.NotLoaded; // PlotSquared not enabled
        PlotAPI api = new PlotAPI();
        Plot currentPlot = api.getPlot(loc);
        if(currentPlot != null) {
            if(currentPlot.isOwner(p.getUniqueId()) || currentPlot.isAdded(p.getUniqueId()) && !currentPlot.isDenied(p.getUniqueId())) {
                return PlotSquaredType.IsAdded; // Can use
            }
            return PlotSquaredType.NoAccess; // Cannot use
        }
        return PlotSquaredType.HasAccess; // No plot
    }

    public boolean gpClaimAccess(Player p, Location loc) {
        if(!HookedGriefPrevention|| !gpEnabled())
            return true;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
        if(claim != null) {
            String errorMessage = claim.allowContainers(p);
            if(errorMessage != null)
                return false;
        }
        return true;
    }

    public boolean townyAccess(Player p, Location loc, Material type) {
        if(!HookedTowny|| !townyEnabled())
            return true;
        if(!PlayerCacheUtil.getCachePermission(p, loc, type.getId(), (byte) 0, TownyPermission.ActionType.SWITCH)) {
            return false;
        }
        return true;
    }

    public boolean checkAccess(Player p, Location loc, Material mat) {
        if(!gpClaimAccess(p, loc) && !Permissions.hasPermission(p, "hiddenstorage.protection.bypass"))
            return false;
        if(!wgRegionAccess(p, loc, mat) && !Permissions.hasPermission(p, "hiddenstorage.protection.bypass"))
            return false;
        if(!townyAccess(p, loc, mat) && !Permissions.hasPermission(p, "hiddenstorage.protection.bypass"))
            return false;
        PlotSquaredType type = psPlotAccess(p, loc);
        if(type == PlotSquaredType.NoAccess && !Permissions.hasPermission(p, "hiddenstorage.protection.bypass")) return false;
        return true;
    }

    private Location NormalizeLocation(Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        World w = loc.getWorld();
        return new Location(w, x, y, z);
    }

}