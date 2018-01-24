package com.songoda.epichoppers.API;

import org.bukkit.inventory.ItemStack;

/**
 * Created by songo on 6/11/2017.
 */
public class SyncCraftAPI {

    public int getILevel(ItemStack item) {
        if (item.getItemMeta().getDisplayName().contains("§fHop§fper")) {
            String lev = item.getItemMeta().getDisplayName().replace("§eLevel ", "");
            return Integer.parseInt(lev.replace(" §fHop§fper", ""));
        } else {
            if (item.getItemMeta().getDisplayName().contains(":")) {
                String arr[] = (item.getItemMeta().getDisplayName().replace("§", "")).split(":");
                return Integer.parseInt(arr[0]);
            } else {
                return 1;
            }
        }
    }
}
