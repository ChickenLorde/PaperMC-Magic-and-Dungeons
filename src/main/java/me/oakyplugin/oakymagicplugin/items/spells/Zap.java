package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;

public class Zap extends Spell {

    public Zap() {
        super(new SpellStats(8, 15, 1.5));

        addStatLore("Damage",4,"red");
        buildLore("\"Shoot a short bolt of electricity that arcs up to 3 targets\"");
    }


    @Override
    public boolean cast(LivingEntity caster, Location location) {
        Location loc = location.clone();
        Vector dir = location.getDirection().clone();

        HashSet<LivingEntity> hitTargets = new HashSet<>();

        double maxDistance = 8;
        double distanceTraveled = 0;

        chainEntities:
        while(distanceTraveled < maxDistance && hitTargets.size() < 3) {
            distanceTraveled += 0.1;
            if(Utility.shieldCollisionCheck(loc, dir.clone().multiply(0.1), 4))
                return true;
            loc.add(dir.clone().multiply(0.1));



            Particle.WAX_OFF.builder()
                    .location(loc)
                    .count(1)
                    .offset(0.02, 0.02, 0.02)
                    .extra(0.8)
                    .spawn();

            HashSet<LivingEntity> potentialTargets = new HashSet<>(loc.getNearbyLivingEntities(0.5));

            for(LivingEntity e : potentialTargets) {
                if(e.equals(caster) || hitTargets.contains(e)) continue;
                hitTargets.add(e);
                maxDistance = 4;
                distanceTraveled = 0;

                HashSet<LivingEntity> nextEntities = new HashSet<>(loc.getNearbyLivingEntities(4));
                LivingEntity closestEntity = null;
                double closest = Double.MAX_VALUE;

                for(LivingEntity le : nextEntities) {
                    if(le.equals(e) || le.equals(caster) || hitTargets.contains(le)) continue;
                    double distance = le.getLocation().distanceSquared(e.getLocation());
                    if(distance < closest) {
                        closest = distance;
                        closestEntity = le;
                    }
                }

                if(closestEntity != null)
                    dir = closestEntity.getBoundingBox().getCenter().clone().subtract(loc.toVector()).normalize();
                else
                    break chainEntities;

                break;
            }
        }

        for(LivingEntity e : hitTargets) {

            Particle.WAX_OFF.builder()
                    .location(e.getBoundingBox().getCenter().toLocation(caster.getWorld()))
                    .count(35)
                    .offset(0.02, 0.02, 0.02)
                    .extra(10)
                    .spawn();
            damageEntity(caster, e, 4);
        }
        return true;
    }

    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Zap"));

        item.lore(getLore());

        return item;
    }
}
