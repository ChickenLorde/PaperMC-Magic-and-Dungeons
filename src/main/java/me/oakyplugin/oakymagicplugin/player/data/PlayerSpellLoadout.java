package me.oakyplugin.oakymagicplugin.player.data;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import me.oakyplugin.oakymagicplugin.items.ItemManager;
import me.oakyplugin.oakymagicplugin.items.Usable;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Locale;

public class PlayerSpellLoadout {

    private PlayerData data;
    private HashSet<String> spellIds;
    private final ItemManager itemManager;

    public PlayerSpellLoadout(PlayerData data) {
        this.data = data;
        spellIds = new HashSet<>();
        itemManager = OakyMagicPlugin.getPlugin().getItemManager();
    }


    public boolean addSpell(String id) {
        boolean b = spellIds.add(id.toLowerCase(Locale.ROOT));
        validateSpells();
        return b;
    }
    public boolean removeSpell(String id) {
        return spellIds.remove(id.toLowerCase(Locale.ROOT));
    }

    public void validateSpells() {
        HashSet<String> toRemove = new HashSet<>();
        for(String id : spellIds) {
            CustomItem item = itemManager.getCustomItemByName(id);
            if(!(item instanceof Spell))
                toRemove.add(id);
        }
        spellIds.removeAll(toRemove);
    }

    public void openSpellLoadout() {
        Inventory inv = Bukkit.createInventory(data.getPlayer(), 9, Component.text("Spell Loadout"));

        int i = 0;
        for(String s : spellIds) {
            Usable usable = itemManager.getUsable(s);
            if(!(usable instanceof Spell spell)) continue;

            ItemStack item = spell.getItem(1);

            inv.setItem(i, item);
            i++;
        }
        data.getPlayer().openInventory(inv);
        data.resetRightClick();
    }

    public String[] getSpellIds() {
        String[] ids = new String[spellIds.size()];

        int i = 0;
        for(String s : spellIds) {
            ids[i] = s;
            i++;
        }
        return ids;
    }
}
