package me.oakyplugin.oakymagicplugin.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public abstract class Usable extends CustomItem {

    private static final NamespacedKey usableKey = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "usable");
    public final static NamespacedKey itemIdKey = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "item_id");

    protected boolean repeatOnHold = true;

    @SuppressWarnings("UnstableApiUsage")
    public static void makeUsable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(usableKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);

        item.unsetData(DataComponentTypes.FOOD);
        item.unsetData(DataComponentTypes.ENCHANTMENTS);
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds(1000000)
                .hasConsumeParticles(false)
                .build());
    }
    public static boolean isUsable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;
        return meta.getPersistentDataContainer().has(usableKey, PersistentDataType.BOOLEAN);
    }

    protected ItemStack getUsable(Material material) {
        ItemStack item = new ItemStack(material);
        makeUsable(item);
        return item;
    }

    public void onResetCharge(Player p) {}

    public abstract void onUse(Player p, ItemStack item);
    public void onRelease(Player p, ItemStack item) {}
    public boolean repeatOnHold() {return repeatOnHold;}
}
