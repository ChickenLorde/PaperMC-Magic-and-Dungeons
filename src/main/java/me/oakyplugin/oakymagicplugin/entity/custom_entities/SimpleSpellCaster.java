package me.oakyplugin.oakymagicplugin.entity.custom_entities;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.LootPool;
import me.oakyplugin.oakymagicplugin.entity.MagicEntity;
import me.oakyplugin.oakymagicplugin.entity.MagicStats;
import me.oakyplugin.oakymagicplugin.items.materials.ManaPowder;
import me.oakyplugin.oakymagicplugin.items.spells.ManaBolt;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import me.oakyplugin.oakymagicplugin.projectiles.LinearProjectile;
import me.oakyplugin.oakymagicplugin.utility.BiPair;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Random;

/// Approaches targets and fires one spell from a set distance on a set interval
public abstract class SimpleSpellCaster extends MagicEntity {

    private int spellChargeTicks;

    protected LivingEntity target;

    private final Random random;
    private final Spell spell;

    private final NamespacedKey freezeKey = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "entity_cast_freeze");

    private final double castDistance, stopDistance, spreadYaw, spreadPitch;
    private final int minCastInterval, maxCastInterval;


    public SimpleSpellCaster(Class<? extends LivingEntity> entityClass, MagicStats stats, LootPool lootPool, int rolls, Class<? extends Spell> spell, double castDistance, double stopDistance, int minCastInterval, int maxCastInterval, double spreadYaw, double spreadPitch) {
        super(entityClass, stats, lootPool, rolls);

        random = new Random();

        this.spell = OakyMagicPlugin.getPlugin().getItemManager().getCustomItem(spell);

        this.castDistance = Math.pow(castDistance,2);
        this.stopDistance = Math.pow(stopDistance,2);
        this.minCastInterval = minCastInterval;
        this.maxCastInterval = maxCastInterval;
        this.spreadYaw = spreadYaw;
        this.spreadPitch = spreadPitch;
    }

    @Override
    protected void tick() {
        if(getTicksAlive() % 20 == 0)
            updateTarget();


        AttributeInstance mvmt = getEntity().getAttribute(Attribute.MOVEMENT_SPEED);
        if(target != null && target.isValid() && getEntity().hasLineOfSight(target)) {
            double distanceToTarget = target.getLocation().distanceSquared(getEntity().getLocation());
            if(mvmt != null) {
                if (distanceToTarget < stopDistance) {
                    if (mvmt.getModifier(freezeKey) == null)
                        mvmt.addModifier(new AttributeModifier(freezeKey, -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                }
                else if(mvmt.getModifier(freezeKey) != null)
                    mvmt.removeModifier(freezeKey);
            }

            if(spellChargeTicks > 0 && spellChargeTicks % random.nextInt(minCastInterval,maxCastInterval + 1)  == 0 || spellChargeTicks > maxCastInterval + 1) {
                Location loc = getEntity().getEyeLocation().clone();
                loc.setDirection(target.getBoundingBox().getCenter().clone().subtract(getEntity().getEyeLocation().toVector()).normalize());
                BiPair<Double, Double> rotation = Utility.VectorToRotation(loc.getDirection());
                double yaw = rotation.left();
                double pitch = rotation.right();
                yaw += random.nextDouble(-spreadYaw, spreadYaw);
                pitch += random.nextDouble(-spreadPitch, spreadPitch);
                Vector dir = Utility.RotationToVector(yaw, pitch, 1);
                loc.setDirection(dir);

                spell.cast(getEntity(), loc);
                Collection<LinearProjectile> projectiles = OakyMagicPlugin.getPlugin().getProjectileManager().getOwnerProjectiles(getEntity());
                for(LinearProjectile p : projectiles) {
                    p.ignoreEntityType(getEntity().getType());
                }

                spellChargeTicks = 0;
            }

            if(distanceToTarget < castDistance)
                spellChargeTicks++;
        } else {
            if(mvmt != null)
                mvmt.removeModifier(freezeKey);
            spellChargeTicks = 0;
        }
    }

    protected abstract void updateTarget();
}
