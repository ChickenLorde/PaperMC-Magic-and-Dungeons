package me.oakyplugin.oakymagicplugin.items.consumables;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class StarInABottle extends ConsumableItem {

    public StarInABottle() {
        super(2);
    }

    @Override
    protected ItemStack customizeConsumable(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Star in a Bottle"));
        item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds(item.getData(DataComponentTypes.CONSUMABLE).consumeSeconds())
                .hasConsumeParticles(false)
                .sound(Key.key("minecraft:entity.generic.drink"))
                .animation(ItemUseAnimation.DRINK));
        return item;
    }

    @Override
    public void onConsume(Player p, ItemStack item) {
        PlayerData data = OakyMagicPlugin.getPlugin().getPlayerManager().getPlayerData(p);

        data.getStats().addMana(25);

        if(p.getGameMode() != GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
            p.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
        }
    }
}
