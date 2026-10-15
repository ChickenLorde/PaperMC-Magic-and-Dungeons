package me.oakyplugin.oakymagicplugin.items.accessories;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.player.data.StatBonus;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class GodStatsAccessory extends Accessory {

    public GodStatsAccessory() {
        super(StatBonus.builder()
                .maxManaBonus(1000000)
                .maxManaRegenBonus(1000000)
                .manaRegenRateBonus(100)
                .build());
    }


    @Override
    protected ItemStack customizeAccessory(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("GOD_STATS_ACCESSORY"));
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return item;
    }
}
