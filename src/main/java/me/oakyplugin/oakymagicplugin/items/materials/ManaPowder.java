package me.oakyplugin.oakymagicplugin.items.materials;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("UnstableApiUsage")
public class ManaPowder extends CustomItem {

    @Override
    protected ItemStack customizeItem(ItemStack item) {

        item.unsetData(DataComponentTypes.CONSUMABLE);
        item.unsetData(DataComponentTypes.FOOD);
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Mana Powder"));
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
            .addString("bush_mana_powder")
            .build());

        item.setData(DataComponentTypes.LORE, ItemLore.lore()
            .addLine(MiniMessage.miniMessage().deserialize("<dark_gray>\"Usually, mana exists as intangible energy"))
            .addLine(MiniMessage.miniMessage().deserialize("<dark_gray>that cannot be directly interacted with."))
            .addLine(MiniMessage.miniMessage().deserialize("<dark_gray>However, if enough mana is concentrated in an"))
            .addLine(MiniMessage.miniMessage().deserialize("<dark_gray>area, it can manifest into a physical, powdery"))
            .addLine(MiniMessage.miniMessage().deserialize("<dark_gray>substance\""))
            .build()
        );


        return item;
    }
}
