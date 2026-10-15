package me.oakyplugin.oakymagicplugin.items.consumables.treasure_bag;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.consumables.ConsumableItem;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TreasureBag extends ConsumableItem {

    public TreasureBag() {
        super();
    }

    @Override
    protected ItemStack customizeConsumable(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Treasure Bag"));
        item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds(item.getData(DataComponentTypes.CONSUMABLE).consumeSeconds())
                .hasConsumeParticles(false)
                .sound(Key.key("minecraft:item.bundle.drop_contents")));
        return item;
    }

    @Override
    public void onConsume(Player p, ItemStack item) {



    }
}
