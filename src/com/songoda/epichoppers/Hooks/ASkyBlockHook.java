package com.songoda.epichoppers.Hooks;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Created by songoda on 3/17/2017.
 */
public class ASkyBlockHook implements Hooks {

    ASkyBlockAPI as = ASkyBlockAPI.getInstance();

    @Override
    public boolean canBuild(Player p, Location location) {
        try {
            if (as.getIslandAt(location) != null) {
                UUID owner = as.getOwner(location);
                List<UUID> list = as.getTeamMembers(owner);
                if (owner != null) {
                    for (UUID uuid : list) {
                        if (uuid.equals(p.getUniqueId())) {
                            return true;
                        }
                    }
                    if (owner.equals(p.getUniqueId())) {
                        return true;
                    }
                    return false;
                }
            }
        } catch (Exception e) {
        }
        return true;
    }
}
