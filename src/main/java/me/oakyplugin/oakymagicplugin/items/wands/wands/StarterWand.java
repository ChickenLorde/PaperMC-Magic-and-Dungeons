package me.oakyplugin.oakymagicplugin.items.wands.wands;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.items.wands.Staff;
import me.oakyplugin.oakymagicplugin.player.data.StatBonus;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StarterWand extends Staff {

    private final StatBonus bonus;

    public StarterWand() {
        bonus = StatBonus.builder()
                .maxManaBonus(25)
                .build();
    }


    @Override
    protected ItemStack customizeStaff(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Starter Wand"));
        return item;
    }

    public StatBonus getBonus() {return bonus;}


}
