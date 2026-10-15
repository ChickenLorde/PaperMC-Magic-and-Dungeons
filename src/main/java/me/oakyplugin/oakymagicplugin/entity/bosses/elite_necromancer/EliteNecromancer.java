package me.oakyplugin.oakymagicplugin.entity.bosses.elite_necromancer;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import me.oakyplugin.oakymagicplugin.entity.LootPool;
import me.oakyplugin.oakymagicplugin.entity.MagicStats;
import me.oakyplugin.oakymagicplugin.entity.bosses.BossAttack;
import me.oakyplugin.oakymagicplugin.entity.bosses.BossEntity;
import me.oakyplugin.oakymagicplugin.items.materials.ManaPowder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

@SuppressWarnings("UnstableApiUsage")
public class EliteNecromancer extends BossEntity {


    public EliteNecromancer() {
        super(WitherSkeleton.class, MagicStats.builder()
                .magicArmor(10)
                .damageMultiplier(0.85)
                .build(), LootPool.builder()
                .addEntry(ManaPowder.class, 1, 1)
                .build(), 1);

        bossName = "Elite Necromancer";

        attacks.add(new SummonWispAttack(this));
        attacks.add(new SummonMinionsAttack(this));
    }

    @Override
    protected void onBossSpawn() {
        AttributeInstance maxHP = getEntity().getAttribute(Attribute.MAX_HEALTH);
        if(maxHP != null) maxHP.setBaseValue(175);
        getEntity().setHealth(175);

        AttributeInstance armor = getEntity().getAttribute(Attribute.ARMOR);
        if(armor != null) armor.setBaseValue(6);

        AttributeInstance scale = getEntity().getAttribute(Attribute.SCALE);
        if(scale != null) scale.setBaseValue(1.05);

        AttributeInstance follow = getEntity().getAttribute(Attribute.FOLLOW_RANGE);
        if(follow != null) follow.setBaseValue(64);

        AttributeInstance kbR = getEntity().getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        if(kbR != null) kbR.setBaseValue(2/3.0);

        AttributeInstance fallDmg = getEntity().getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER);
        if(fallDmg != null) fallDmg.setBaseValue(0);

        EntityEquipment equipment = getEntity().getEquipment();

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        helmet.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        chestplate.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        leggings.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        boots.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);

        helmet.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor()
                        .color(Color.fromRGB(0,0,0))
                        .build());
        chestplate.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor()
                .color(Color.fromRGB(0,0,0))
                .build());
        leggings.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor()
                .color(Color.fromRGB(0,0,0))
                .build());
        boots.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor()
                .color(Color.fromRGB(0,0,0))
                .build());

        equipment.setHelmet(helmet);
        equipment.setChestplate(chestplate);
        equipment.setLeggings(leggings);
        equipment.setBoots(boots);
        equipment.setItemInMainHand(new ItemStack(Material.STICK));
        equipment.setDropChance(EquipmentSlot.HEAD, 0);
        equipment.setDropChance(EquipmentSlot.CHEST, 0);
        equipment.setDropChance(EquipmentSlot.LEGS, 0);
        equipment.setDropChance(EquipmentSlot.FEET, 0);
        equipment.setDropChance(EquipmentSlot.HAND, 0);
    }

    @Override
    protected void bossTick() {
        Collections.shuffle(attacks);
        if(attackCooldown == 0 && activeAttack == null) {
            for(BossAttack a : attacks)
                if(a.activateAttack())
                    break;
        } else if(activeAttack != null) {
            activeAttack.attackTick();
        }
    }

    public void cancelAttacks() {
        if(activeAttack == null) return;
        activeAttack.cancelAttack();
    }

    @Override
    public WitherSkeleton getEntity() {
        return (WitherSkeleton)super.getEntity();
    }
}
