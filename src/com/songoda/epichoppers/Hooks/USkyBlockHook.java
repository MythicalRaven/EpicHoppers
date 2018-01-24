package com.songoda.epichoppers.Hooks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

import java.util.List;

/**
 * Created by songoda on 3/17/2017.
 */
public class USkyBlockHook implements Hooks {

    Plugin plugin = Bukkit.getPluginManager().getPlugin("uSkyBlock");

    @Override
    public boolean canBuild(Player p, Location location) {

        uSkyBlockAPI usb = (uSkyBlockAPI) plugin;

        List<Player> list = usb.getIslandInfo(location).getOnlineMembers();



        for (Player pl : list) {
            if (pl.equals(p)) {
                return true;
            }
        }

        if (usb.getIslandInfo(location).isLeader(p)) {
            return true;
        }
        return false;
    }
}
