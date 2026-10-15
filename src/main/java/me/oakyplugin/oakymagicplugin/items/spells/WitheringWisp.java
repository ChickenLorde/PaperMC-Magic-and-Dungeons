package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import me.oakyplugin.oakymagicplugin.projectiles.LinearProjectile;
import me.oakyplugin.oakymagicplugin.projectiles.ProjectileData;
import me.oakyplugin.oakymagicplugin.projectiles.TrackingProjectile;
import me.oakyplugin.oakymagicplugin.projectiles.TrackingProjectileData;
import me.oakyplugin.oakymagicplugin.status_effect.effects.Decay;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashMap;

public class WitheringWisp extends TrackingProjectileSpell {

    public WitheringWisp() {
        //1.1
        super(new SpellStats(14, 30, 2),
                ProjectileData.builder()
                        .damage(5)
                        .speed(0.8)
                        .maxTicksAlive(200)
                        .collisionSize(0.35)
                        .updatesPerTick(2)
                        .build(),
                TrackingProjectileData.builder()
                        .turnSpeed(0.35)
                        .acceleration(0.11)
                        .dragCoefficient(0.17)
                        .build());
        addStatLore("Damage",5,"red",0);
        buildLore("\"Summon a vengeful spirit that seeks out its target, eating away at their soul upon contact\"");
    }

    @Override
    public boolean cast(LivingEntity caster, Location location) {

        Location loc = location.clone();
        Vector dir = location.getDirection().clone();

        HashMap<LivingEntity, Double> potentialTargets = new HashMap<>();
        for(int i = 0; i < 100; i++) {
            Collection<LivingEntity> entities = loc.getWorld().getNearbyLivingEntities(loc, 5);

            for(LivingEntity e : entities) {
                if(e.equals(caster)) continue;

                double currentDistance = e.getLocation().distanceSquared(loc);
                if(!potentialTargets.containsKey(e)) potentialTargets.put(e, currentDistance);
                else {
                    double distance = potentialTargets.get(e);
                    if(currentDistance < distance)
                        potentialTargets.replace(e, currentDistance);
                }
            }
            loc.add(dir.clone().multiply(0.5));
        }
        double closest = Double.MAX_VALUE;
        LivingEntity target = null;

        for(LivingEntity e : potentialTargets.keySet()) {
            double distance = potentialTargets.get(e);
            if(distance < closest) {
                closest = distance;
                target = e;
            }
        }

        if(target == null) {
            if(!(caster instanceof Player p)) return false;
            PlayerData data = plugin.getPlayerManager().getPlayerData(p);
            if(data.getRightClickDownTime() == 1)
                caster.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find a target"));
            return false;
        }

        double yaw = Math.random() * Math.PI * 2;

        double max = Utility.deg2Rad(90);
        double min = Utility.deg2Rad(85);
        double range = max - min;

        double pitch = Math.random() * range + min;

        location.getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, 0.65f, 2.0f);
        TrackingProjectile projectile = createProjectile(caster, location, caster.getEyeLocation().getDirection()).setInitialValues()
                .target(target)
                .velocity(Utility.RotateAroundVector(dir, yaw, pitch, 0.8))
                .angle(caster.getEyeLocation().getDirection())
                .setValues();
        location.getWorld().playSound(location, Sound.ENTITY_PHANTOM_AMBIENT,0.7f, (float)Math.random() * 0.15f + 1.85f);
        return true;
    }

    @Override
    public TrackingProjectile castTrackingProjectile(LivingEntity caster, LivingEntity target, Location location) {
        return createProjectile(caster, location, location.getDirection()).setInitialValues()
                .target(target)
                .velocity(new Vector(0,0,0))
                .angle(location.getDirection())
                .setValues();
    }
    public TrackingProjectile castTrackingProjectile(LivingEntity caster, LivingEntity target, Location location, Vector velocity, Vector angle) {
        return createProjectile(caster, location, location.getDirection()).setInitialValues()
                .target(target)
                .velocity(velocity)
                .angle(angle)
                .setValues();
    }



    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Withering Wisp"));

        item.lore(getLore());

        return item;
    }

    @Override
    public void onProjectileTick(Location location, LinearProjectile projectile) {
        Particle.SOUL_FIRE_FLAME.builder()
                .location(location)
                .offset(0.03, 0.03, 0.03)
                .count(1)
                .extra(0.02)
                .receivers(64, true)
                .spawn();
        Particle.SOUL.builder()
                .location(location)
                .offset(0.0, 0.0, 0.0)
                .count(1)
                .extra(0.02)
                .receivers(64, true)
                .spawn();
    }

    @Override
    public void onProjectileEnd(Location location) {
        Particle.SOUL_FIRE_FLAME.builder()
                .location(location)
                .offset(0.03, 0.03, 0.03)
                .count(15)
                .extra(0.08)
                .receivers(64, true)
                .spawn();
    }

    @Override
    public void onEntity(LivingEntity cause, LivingEntity target, Location location) {
        damageEntity(cause, target, getProjectileStats().getDamage(),new Decay(60, target.getUniqueId()));

        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_BURN,0.7f, (float)Math.random() * 0.15f + 1.75f);
        location.getWorld().playSound(location, Sound.ENTITY_PHANTOM_AMBIENT,0.7f, (float)Math.random() * 0.15f + 1.85f);

    }
}
