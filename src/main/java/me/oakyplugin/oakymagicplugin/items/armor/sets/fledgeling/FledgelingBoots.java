package me.oakyplugin.oakymagicplugin.items.armor.sets.fledgeling;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.MagicStats;
import me.oakyplugin.oakymagicplugin.items.armor.ArmorPiece;
import me.oakyplugin.oakymagicplugin.player.data.StatBonus;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class FledgelingBoots extends ArmorPiece {
    public FledgelingBoots() {
        super(ArmorType.FEET, ArmorMaterial.LEATHER, ArmorSet.FLEDGELING, StatBonus.builder()
                .maxManaBonus(10)
                .magicStats(MagicStats.builder()
                        .magicArmor(1)
                        .build())
                .build());
    }

    @Override
    protected ItemStack customizeArmor(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Fledgeling Boots"));
        item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                .addModifier(Attribute.ARMOR, new AttributeModifier(new NamespacedKey(OakyMagicPlugin.NAMESPACE, "fledgeling_boots"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET))
                .build());
        return item;
    }
}
