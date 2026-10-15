package me.oakyplugin.oakymagicplugin.items.wands;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import me.oakyplugin.oakymagicplugin.items.Usable;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public abstract class Staff extends Usable {
    public static final NamespacedKey staffKey = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "staff");

    @Override
    protected final ItemStack customizeItem(ItemStack item) {
        makeUsable(item);
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(staffKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);

        return customizeStaff(item);
    }

    protected abstract ItemStack customizeStaff(ItemStack item);

    public void openSpells(Player p) {
        p.sendMessage(Component.text("Try open spell loadout"));
        OakyMagicPlugin.getPlugin().getPlayerManager().getPlayerData(p).getSpellLoadout().openSpellLoadout();
    }

    @Override
    public void onUse(Player p, ItemStack item) {
        openSpells(p);
    }
}
