package me.oakyplugin.oakymagicplugin.items.consumables;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public abstract class ConsumableItem extends CustomItem {

    private final int consumeSeconds;
    private final boolean instantUse;

    public ConsumableItem(int consumeSeconds) {
        instantUse = false;
        this.consumeSeconds = consumeSeconds;
    }
    public ConsumableItem() {
        instantUse = true;
        consumeSeconds = 0;
    }

    @Override
    protected final ItemStack customizeItem(ItemStack item) {

        if(!instantUse) {
            item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .consumeSeconds(consumeSeconds)
                    .build());
        }
        return customizeConsumable(item);
    }

    protected abstract ItemStack customizeConsumable(ItemStack item);


    public abstract void onConsume(Player p, ItemStack item);
}
