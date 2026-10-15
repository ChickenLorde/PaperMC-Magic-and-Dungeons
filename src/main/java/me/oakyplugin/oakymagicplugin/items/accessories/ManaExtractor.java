package me.oakyplugin.oakymagicplugin.items.accessories;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.materials.ManaPowder;
import me.oakyplugin.oakymagicplugin.player.data.StatBonus;
import me.oakyplugin.oakymagicplugin.utility.ActivateOnKill;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;


@SuppressWarnings("UnstableApiUsage")
public class ManaExtractor extends Accessory implements ActivateOnKill {

    public ManaExtractor() {
        super(null);
    }


    @Override
    protected ItemStack customizeAccessory(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Mana Extractor"));
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return item;
    }

    @Override
    public void activateOnKill(Entity killer) {
        if(!(killer instanceof Player p)) return;

        List<Accessory> accs = Arrays.asList(OakyMagicPlugin.getPlugin().getPlayerManager().getPlayerData(p).getAccessories().getAccessories());
        if(!accs.contains(this)) return;

        int rand = (int)(Math.random() * 2);
        int rand2 = (int)(Math.random() * 2 + 1);

        if(rand == 0) {
            OakyMagicPlugin.getPlugin().getItemManager().getCustomItem(ManaPowder.class).give(rand2, p);
            //p.sendMessage(MiniMessage.miniMessage().deserialize("<green>DEBUG: ACTIVATEONKILL SUCCESS"));
        }
    }
}
