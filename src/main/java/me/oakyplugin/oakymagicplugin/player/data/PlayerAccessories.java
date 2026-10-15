package me.oakyplugin.oakymagicplugin.player.data;


import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.accessories.Accessory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class PlayerAccessories {

    private final PlayerData data;
    private int maxAccessories;
    private final HashSet<String> accessoryIds;

    PlayerAccessories(PlayerData data) {
        this.data = data;
        maxAccessories = 3;
        accessoryIds = new HashSet<>();
    }

    public void openAccessories() {
        Player p = data.getPlayer();
        if(p == null) return;
        Inventory inv = Bukkit.createInventory(p, 9, Component.text("Magic Accessories"));

        HashSet<String> remove = new HashSet<>();
        for(String id : accessoryIds) {
            Accessory accessory = OakyMagicPlugin.getPlugin().getItemManager().getAccessory(id);
            if(accessory == null) {
                remove.add(id);
                continue;
            }
            ItemStack item = accessory.getItem(1);
            inv.addItem(item);
        }
        accessoryIds.removeAll(remove);

        for(int i = 0; i < 9 - maxAccessories; i++) {
            ItemStack locked = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            inv.setItem(8 - i, locked);
        }

        p.openInventory(inv);
    }

    public boolean addAccessory(String id) {
        if(accessoryIds.size() >= maxAccessories) return false;
        return accessoryIds.add(id.toLowerCase(Locale.ROOT));
    }

    public boolean removeAccessory(String id) {
        return accessoryIds.remove(id.toLowerCase(Locale.ROOT));
    }

    public Accessory[] getAccessories() {
        Accessory[] accessories = new Accessory[accessoryIds.size()];

        int i = 0;
        for(String id : accessoryIds) {
            Accessory acc = OakyMagicPlugin.getPlugin().getItemManager().getAccessory(id);
            accessories[i] = acc;
            i++;
        }
        return accessories;
    }
    public List<String> getAccessoryIds() {
        return new ArrayList<>(accessoryIds);
    }


}
