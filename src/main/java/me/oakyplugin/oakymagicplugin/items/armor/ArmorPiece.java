package me.oakyplugin.oakymagicplugin.items.armor;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import me.oakyplugin.oakymagicplugin.player.data.StatBonus;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public abstract class ArmorPiece extends CustomItem {

    public enum ArmorType {
        HEAD,
        CHEST,
        LEGS,
        FEET
    }
    public enum ArmorMaterial {
        LEATHER,
        IRON,
        GOLD,
        DIAMOND,
        NETHERITE,
        CHAINMAIL,
        COPPER
    }

    public enum ArmorSet {
        FLEDGELING
    }

    private final ArmorType type;
    private final ArmorMaterial material;
    private final ArmorSet set;
    private final StatBonus statBonus;

    public ArmorPiece(ArmorType type, ArmorMaterial material, ArmorSet set, StatBonus statBonus) {
        this.type = type;
        this.material = material;
        this.set = set;
        this.statBonus = statBonus;

        switch(type) {
            case HEAD:
                ITEM_MATERIAL = Material.POISONOUS_POTATO;
                break;
            case CHEST:
                switch(material) {
                    case LEATHER: ITEM_MATERIAL = Material.LEATHER_CHESTPLATE; break;
                    case IRON: ITEM_MATERIAL = Material.IRON_CHESTPLATE; break;
                    case GOLD: ITEM_MATERIAL = Material.GOLDEN_CHESTPLATE; break;
                    case DIAMOND: ITEM_MATERIAL = Material.DIAMOND_CHESTPLATE; break;
                    case NETHERITE: ITEM_MATERIAL = Material.NETHERITE_CHESTPLATE; break;
                    case CHAINMAIL: ITEM_MATERIAL = Material.CHAINMAIL_CHESTPLATE; break;
                    case COPPER: ITEM_MATERIAL = Material.COPPER_CHESTPLATE; break;
                }
                break;
            case LEGS:
                switch(material) {
                    case LEATHER: ITEM_MATERIAL = Material.LEATHER_LEGGINGS; break;
                    case IRON: ITEM_MATERIAL = Material.IRON_LEGGINGS; break;
                    case GOLD: ITEM_MATERIAL = Material.GOLDEN_LEGGINGS; break;
                    case DIAMOND: ITEM_MATERIAL = Material.DIAMOND_LEGGINGS; break;
                    case NETHERITE: ITEM_MATERIAL = Material.NETHERITE_LEGGINGS; break;
                    case CHAINMAIL: ITEM_MATERIAL = Material.CHAINMAIL_LEGGINGS; break;
                    case COPPER: ITEM_MATERIAL = Material.COPPER_LEGGINGS; break;
                }
                break;
            case FEET:
                switch(material) {
                    case LEATHER: ITEM_MATERIAL = Material.LEATHER_BOOTS; break;
                    case IRON: ITEM_MATERIAL = Material.IRON_BOOTS; break;
                    case GOLD: ITEM_MATERIAL = Material.GOLDEN_BOOTS; break;
                    case DIAMOND: ITEM_MATERIAL = Material.DIAMOND_BOOTS; break;
                    case NETHERITE: ITEM_MATERIAL = Material.NETHERITE_BOOTS; break;
                    case CHAINMAIL: ITEM_MATERIAL = Material.CHAINMAIL_BOOTS; break;
                    case COPPER: ITEM_MATERIAL = Material.COPPER_BOOTS; break;
                }
                break;
        }


    }

    @Override
    protected final ItemStack customizeItem(ItemStack item) {
        item.setData(DataComponentTypes.UNBREAKABLE);
        item.unsetData(DataComponentTypes.ENCHANTMENTS);
        item.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        item.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        return customizeArmor(item);
    }

    protected abstract ItemStack customizeArmor(ItemStack item);

    public StatBonus getStatBonus() {return statBonus;}
}
