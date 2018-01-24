package com.songoda.epichoppers.Utils;

import com.songoda.arconix.Arconix;
import com.songoda.epichoppers.Lang;
import com.songoda.epichoppers.EpicHoppers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by songo on 6/4/2017.
 */
public class SettingsManager implements Listener {

    EpicHoppers plugin = EpicHoppers.pl();

    public Map<Player, Integer> page = new HashMap<>();

    private static ConfigWrapper defs;

    public SettingsManager() {
        plugin.saveResource("SettingDefinitions.yml", true);
        defs = new ConfigWrapper(plugin, "", "SettingDefinitions.yml");
        defs.createNewFile("Loading data file", "EpicHoppers SettingDefinitions file");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Map<Player, String> current = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getInventory().getTitle().equals("EpicHoppers Settings Editor")) {
                e.setCancelled(true);
                if (e.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE)) {
                    String key = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);

                    Player p = (Player) e.getWhoClicked();

                    if (plugin.getConfig().get("settings." + key).getClass().getName().equals("java.lang.Boolean")) {
                        boolean bool = (Boolean) plugin.getConfig().get("settings." + key);
                        if (!bool)
                            plugin.getConfig().set("settings." + key, true);
                        else
                            plugin.getConfig().set("settings." + key, false);
                        finishEditing(p);
                    } else {
                        editObject(p, key);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (current.containsKey(p)) {
            if (plugin.getConfig().get("settings." + current.get(p)).getClass().getName().equals("java.lang.Integer")) {
                plugin.getConfig().set("settings." + current.get(p), Integer.parseInt(e.getMessage()));
            } else if (plugin.getConfig().get("settings." + current.get(p)).getClass().getName().equals("java.lang.Double")) {
                plugin.getConfig().set("settings." + current.get(p), Double.parseDouble(e.getMessage()));
            } else if (plugin.getConfig().get("settings." + current.get(p)).getClass().getName().equals("java.lang.String")) {
                plugin.getConfig().set("settings." + current.get(p), e.getMessage());
            }
            finishEditing(p);
            e.setCancelled(true);
        }
    }

    public void finishEditing(Player p) {
        current.remove(p);
        plugin.saveConfig();
        openEditor(p);
    }


    public void editObject(Player p, String current) {
        this.current.put(p, current);
        p.closeInventory();
        p.sendMessage("");
        p.sendMessage(Arconix.pl().format().formatText("&7Please enter a value for &6" + current + "&7."));
        if (plugin.getConfig().get("settings." + current).getClass().getName().equals("java.lang.Integer")) {
            p.sendMessage(Arconix.pl().format().formatText("&cUse only numbers."));
        }
        p.sendMessage("");
    }

    public void openEditor(Player p) {
        int pmin = 1;

        if (page.containsKey(p))
            pmin = page.get(p);

        if (pmin != 1)
            pmin = 45;

        int pmax = pmin * 44;

        Inventory i = Bukkit.createInventory(null, 54, "EpicHoppers Settings Editor");

        int num = 0;
        int total = 0;
        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("settings");
        for (String key : cs.getKeys(true)) {
            if (!key.contains("levels")) {
                if (total >= pmin - 1 && total <= pmax) {

                    ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(Arconix.pl().format().formatText("&6" + key));
                    ArrayList<String> lore = new ArrayList<>();
                    if (plugin.getConfig().get("settings." + key).getClass().getName().equals("java.lang.Boolean")) {

                        item.setType(Material.LEVER);
                        boolean bool = (Boolean) plugin.getConfig().get("settings." + key);

                        if (!bool)
                            lore.add(Arconix.pl().format().formatText("&c" + bool));
                        else
                            lore.add(Arconix.pl().format().formatText("&a" + bool));

                    } else if (plugin.getConfig().get("settings." + key).getClass().getName().equals("java.lang.String")) {
                        item.setType(Material.PAPER);
                        String str = (String) plugin.getConfig().get("settings." + key);
                        lore.add(Arconix.pl().format().formatText("&9" + str));
                    } else if (plugin.getConfig().get("settings." + key).getClass().getName().equals("java.lang.Integer")) {
                        item.setType(Material.WATCH);

                        int in = (Integer) plugin.getConfig().get("settings." + key);
                        lore.add(Arconix.pl().format().formatText("&5" + in));
                    }
                    if (defs.getConfig().contains(key)) {
                        String text = defs.getConfig().getString(key);
                        int index = 0;
                        while (index < text.length()) {
                            lore.add(Arconix.pl().format().formatText("&7" + text.substring(index, Math.min(index + 30, text.length()))));
                            index += 30;
                        }
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    i.setItem(num, item);
                    num++;
                }
                total++;
            }
        }


        int nu = 45;
        while (nu != 54) {
            i.setItem(nu, Methods.getGlass());
            nu++;
        }


        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        ItemStack skull = head;
        if (!plugin.v1_7)
            skull = Arconix.pl().getGUI().addTexture(head, "http://textures.minecraft.net/texture/1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (plugin.v1_7)
            skullMeta.setOwner("MHF_ArrowRight");
        skull.setDurability((short) 3);
        skullMeta.setDisplayName(Lang.NEXT.getConfigValue());
        skull.setItemMeta(skullMeta);

        ItemStack head2 = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        ItemStack skull2 = head2;
        if (!plugin.v1_7)
            skull2 = Arconix.pl().getGUI().addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
        SkullMeta skull2Meta = (SkullMeta) skull2.getItemMeta();
        if (plugin.v1_7)
            skull2Meta.setOwner("MHF_ArrowLeft");
        skull2.setDurability((short) 3);
        skull2Meta.setDisplayName(Lang.BACK.getConfigValue());
        skull2.setItemMeta(skull2Meta);

        if (pmin != 1) {
            //i.setItem(46, skull2);
        }
        if (pmin == 1) {
            //i.setItem(52, skull);
        }

        p.openInventory(i);
    }
}
