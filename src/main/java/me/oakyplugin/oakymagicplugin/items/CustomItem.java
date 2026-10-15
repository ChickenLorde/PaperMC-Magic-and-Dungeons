package me.oakyplugin.oakymagicplugin.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;

public abstract class CustomItem {
    protected Material ITEM_MATERIAL;

    public CustomItem() {
        ITEM_MATERIAL = Material.POISONOUS_POTATO;
    }

    @SuppressWarnings("UnstableApiUsage")
    public final ItemStack getItem(int count) {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        item.setAmount(count);
        ItemMeta meta = item.getItemMeta();

        if(meta != null) {
            meta.getPersistentDataContainer().set(OakyMagicPlugin.ITEM_ID, PersistentDataType.STRING, getId());
            item.setItemMeta(meta);
        }

        item.unsetData(DataComponentTypes.CONSUMABLE);
        item.unsetData(DataComponentTypes.FOOD);

        item = customizeItem(item);

        return item;
    }
    public final void give(int count, Player p) {
        if(p == null) return;
        Inventory inv = p.getInventory();
        inv.addItem(getItem(count));
    }
    protected abstract ItemStack customizeItem(ItemStack item);


    public String getId() {
        String s = getClass().getSimpleName();
        char[] chars = s.toCharArray();
        StringBuilder formatted = new StringBuilder();
        for(int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if(i == 0 || !Character.isUpperCase(c)) {
                formatted.append(c);
                continue;
            }
            formatted.append("_").append(c);
        }
        return formatted.toString().toLowerCase(Locale.ROOT);
    }
    public String getIdFormatted() {
        String id = getId();

        StringBuilder builder = new StringBuilder(id.replace("_"," "));

        builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));

        for(int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);

            if(c == ' ')
                builder.setCharAt(i+1, Character.toUpperCase(builder.charAt(i+1)));
        }
        return builder.toString();
    }
}
