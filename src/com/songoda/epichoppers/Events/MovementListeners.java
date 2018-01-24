package com.songoda.epichoppers.Events;

import com.songoda.arconix.Arconix;
import com.songoda.epichoppers.EpicHoppers;
import com.songoda.epichoppers.Utils.Methods;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Date;

/**
 * Created by songoda on 3/23/2017.
 */
public class MovementListeners implements Listener {

    private EpicHoppers plugin = EpicHoppers.pl();

    @EventHandler
    public void moveEvent(PlayerMoveEvent e) {
        if (plugin.getConfig().getBoolean("settings.Teleport-hoppers") && e.getPlayer().hasPermission("EpicHoppers.Teleport")) {
            Location location = e.getTo().clone();
            location.setY(location.getY() - 0.5);
            if (plugin.dataFile.getConfig().contains("data.sync." + Arconix.pl().serialize().serializeLocation(location.getBlock()) + ".block")) {
                if (plugin.dataFile.getConfig().contains("data.sync." + Arconix.pl().serialize().serializeLocation(location.getBlock()) + ".telewalk")) {
                    if (plugin.lastTp.containsKey(e.getPlayer())) {
                        long duration = (new Date()).getTime() - plugin.lastTp.get(e.getPlayer()).getTime();
                        if (duration <= 5 * 1000) {
                            return;
                        }
                    }
                    Methods.tpPlayer(e.getPlayer(), location.getBlock());
                    plugin.lastTp.put(e.getPlayer(), new Date());

                }
            }
        }
    }
}
