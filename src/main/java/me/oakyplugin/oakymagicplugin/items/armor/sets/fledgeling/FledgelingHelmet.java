package me.oakyplugin.oakymagicplugin.items.armor.sets.fledgeling;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.MagicStats;
import me.oakyplugin.oakymagicplugin.items.armor.ArmorPiece;
import me.oakyplugin.oakymagicplugin.player.data.StatBonus;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class FledgelingHelmet extends ArmorPiece {
    public FledgelingHelmet() {
        super(ArmorType.HEAD, ArmorMaterial.LEATHER, ArmorSet.FLEDGELING, StatBonus.builder()
                .maxManaBonus(10)
                .magicStats(MagicStats.builder()
                        .magicArmor(1)
                        .build())
                .build());
    }

    @Override
    protected ItemStack customizeArmor(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Fledgeling Hat"));
        item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                .addModifier(Attribute.ARMOR, new AttributeModifier(new NamespacedKey(OakyMagicPlugin.NAMESPACE, "fledgeling_helmet"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD))
                .build());
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                        .addString("bush_fledgeling_hat")
                        .build());

        item.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HEAD)
                        .equipSound(new NamespacedKey("minecraft","item.armor.equip_leather"))
                        .build());
        return item;
    }
}
