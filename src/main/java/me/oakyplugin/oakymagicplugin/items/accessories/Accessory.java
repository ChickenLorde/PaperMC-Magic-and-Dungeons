package me.oakyplugin.oakymagicplugin.items.accessories;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import me.oakyplugin.oakymagicplugin.player.data.StatBonus;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public abstract class Accessory extends CustomItem {
    public static final NamespacedKey accessoryKey = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "accessory");

    private final StatBonus statBonus;

    public Accessory(StatBonus bonus) {
        statBonus = bonus;
    }

    @Override
    protected final ItemStack customizeItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(accessoryKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        customizeAccessory(item);
        return item;
    }
    protected abstract ItemStack customizeAccessory(ItemStack item);

    public StatBonus getStatBonus() {return statBonus; }
}
