package me.oakyplugin.oakymagicplugin.entity.custom_entities;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.entity.LootPool;
import me.oakyplugin.oakymagicplugin.entity.MagicStats;
import me.oakyplugin.oakymagicplugin.items.materials.ManaPowder;
import me.oakyplugin.oakymagicplugin.items.spells.Icicle;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Stray;
import org.bukkit.inventory.ItemStack;

public class IcicleThrower extends SimpleSpellCaster {

    public IcicleThrower() {
        super(Stray.class, new MagicStats(), LootPool.builder()
                .addEntry(ManaPowder.class, 1, 2)
                .addEntry(ManaPowder.class, 1, 3)
                .addEntry(ManaPowder.class, 1, 4)
                .build()
        ,1, Icicle.class, 18, 7,60, 75,Math.PI / 28.0, Math.PI / 48.0);
    }

    @Override
    protected void onSpawn() {
        Stray z = getEntity();
        z.getEquipment().setItemInMainHand(new ItemStack(Material.STICK));
        ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
        helm.setData(DataComponentTypes.UNBREAKABLE);
        z.getEquipment().setHelmet(helm);

        AttributeInstance atk = z.getAttribute(Attribute.ATTACK_DAMAGE);
        if(atk != null) atk.setBaseValue(0);
        AttributeInstance mvmt = z.getAttribute(Attribute.MOVEMENT_SPEED);
        if(mvmt != null) mvmt.setBaseValue(0.21);

        z.customName(Component.text("Icicle Caster"));
    }

    @Override
    public Stray getEntity() {
        return (Stray)super.getEntity();
    }

    @Override
    protected void updateTarget() {
        this.target = getEntity().getTarget();
    }
}
