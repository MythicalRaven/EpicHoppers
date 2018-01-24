package com.songoda.epichoppers.Events;

import com.songoda.arconix.Arconix;
import com.songoda.epichoppers.EpicHoppers;
import com.songoda.epichoppers.Utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Created by songoda on 4/18/2017.
 */
public class HopperListeners implements Listener {

    private EpicHoppers plugin = EpicHoppers.pl();

    @EventHandler
    public void onHop(InventoryMoveItemEvent e) {
        if (e.getSource().getHolder() instanceof Hopper) {
            String key = Arconix.pl().serialize().serializeLocation(((Hopper) e.getSource().getHolder()).getLocation());
            if (plugin.dataFile.getConfig().contains("data.sync." + key + ".block")) {
                if (Arconix.pl().serialize().unserializeLocation(key).getBlock() != null) {
                    e.setCancelled(true);
                }
            }
        }
    }

    Map<UUID, Player> ents = new HashMap<>();

    @EventHandler
    public void onDed(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if (Methods.isSync(p)) {
                double d = ((LivingEntity)e.getEntity()).getHealth() - e.getDamage();
                if (d < 1) {
                    ents.put(e.getEntity().getUniqueId(), p);
                }
            }
        }
    }

    @EventHandler
    public void ondrop(EntityDeathEvent e) {
        if (ents.containsKey(e.getEntity().getUniqueId())) {
            Player p = ents.get(e.getEntity().getUniqueId());

            ItemStack item = p.getItemInHand();
            ItemMeta meta = item.getItemMeta();
            Location location = Arconix.pl().serialize().unserializeLocation(meta.getLore().get(1).replaceAll("§", ""));
            if (location.getBlock().getType() == Material.CHEST) {
                InventoryHolder ih = (InventoryHolder) location.getBlock().getState();
                for (ItemStack is : e.getDrops()) {
                    ih.getInventory().addItem(is);
                }
                e.getDrops().clear();
            }
        }
    }
}
