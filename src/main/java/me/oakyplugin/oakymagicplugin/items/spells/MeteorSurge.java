package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.items.TickableItem;
import me.oakyplugin.oakymagicplugin.status_effect.effects.Burn;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MeteorSurge extends Spell implements TickableItem {

    private final Map<LivingEntity, MeteorPlayerData> meteorTickProgress;

    public MeteorSurge() {
        super(new SpellStats(50, 200, 30, 20, 20, true));

        meteorTickProgress = new HashMap<>();

        addStatLore("Damage",25, "red");
        buildLore("\"Ignite and ascend into the sky, charging at foes in a blazing inferno\"");
    }

    @Override
    public boolean cast(LivingEntity caster, Location location) {
        meteorTickProgress.put(caster, new MeteorPlayerData());
        caster.setVelocity(new Vector(0,1.5,0));
        return true;
    }

    @Override
    public boolean usePrecondition(Player p) {
        RayTraceResult result = p.getWorld().rayTraceBlocks(p.getLocation(), new Vector(0, -1, 0), 1.2);

        return result != null && result.getHitBlock() != null && !result.getHitBlock().isPassable();
    }

    @Override
    public void tick() {
        if(meteorTickProgress.isEmpty()) return;

        HashSet<LivingEntity> entitiesToRemove = new HashSet<>();

        for(LivingEntity e : meteorTickProgress.keySet()) {

            if(e.isDead() || !e.isValid()) {
                entitiesToRemove.add(e);
                continue;
            }

            MeteorPlayerData data = meteorTickProgress.get(e);
            data.ticks++;
            if(data.isDescending) {
                int tick = data.ticks;
                    descend(e, data);
                if(tick > 18 || !e.isValid())
                    entitiesToRemove.add(e);
            }
            else if(e.getVelocity().getY() < 0)
                startDescend(e, data);
            else {
                for(int i = 0; i < 15; i ++) {
                    double angle = Math.random() * Math.PI * 2;
                    double spread = Math.random() * 0.4;

                    Location spawnLoc = e.getLocation().clone().add(Math.cos(angle) * spread, 0, Math.sin(angle) * spread);

                    Particle.FLAME.builder()
                            .location(spawnLoc)
                            .offset(0, Math.random() * -0.2 - 0.1, 0)
                            .extra(1)
                            .count(0)
                            .spawn();
                }
                Particle.LAVA.builder()
                        .location(e.getLocation())
                        .count(2)
                        .spawn();
            }
        }
        meteorTickProgress.keySet().removeAll(entitiesToRemove);
    }

    @Override
    public void chargeTick(Player p, Location location, int chargeTicks) {

        Particle.FLAME.builder()
                .location(p.getLocation())
                .extra(0.2)
                .count(2)
                .spawn();

        if(chargeTicks >= getStats().chargeTicks()) {
            Particle.LAVA.builder()
                    .location(p.getLocation())
                    .extra(0.3)
                    .count(4)
                    .spawn();
            Particle.FLAME.builder()
                    .location(p.getLocation())
                    .extra(0.4)
                    .count(2)
                    .spawn();
        }

    }

    @Override
    public void onMaxCharge(Player p, Location location) {
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 2.0f);

        double sphereYaw;
        double spherePitch;

        Location origin = p.getLocation().clone().add(0,p.getHeight() * 0.5, 0);

        for(double i = 0; i < 360; i+= 1) {
            spherePitch = 2 * Math.PI / 360 * i;
            sphereYaw = i * 37;
            double mag = 0.1;

            Location spawnLoc = origin.clone().add(
                    Math.cos(sphereYaw) * Math.cos(spherePitch) * mag,
                    Math.sin(spherePitch) * mag,
                    Math.sin(sphereYaw) * Math.cos(spherePitch) * mag
            );
            Location move = origin.clone().add(
                    Math.cos(sphereYaw) * Math.cos(spherePitch) * (mag + 0.2),
                    Math.sin(spherePitch) * (mag + 0.2),
                    Math.sin(sphereYaw) * Math.cos(spherePitch) * (mag + 0.2)).subtract(spawnLoc);

            Particle.FLAME.builder()
                    .location(spawnLoc)
                    .count(0)
                    .offset(move.getX(),move.getY(),move.getZ())
                    .extra(1)
                    .force(true)
                    .spawn();

        }
    }

    private void startDescend(LivingEntity e, MeteorPlayerData m) {
        m.descendDir = e.getEyeLocation().getDirection().clone();
        m.isDescending = true;
        m.ticks = 0;
    }

    private void descend(LivingEntity le, MeteorPlayerData m) {
        final double DESCEND_SPEED = 2.5;

        Vector velocity = m.descendDir.clone().multiply(DESCEND_SPEED);

        le.setFallDistance(0);

        BoundingBox box = le.getBoundingBox().clone();
        box.expand(0.75);

        if(le.wouldCollideUsing(box))
            collide(le);

        Collection<Entity> nearby = le.getWorld().getNearbyEntities(le.getBoundingBox());
        for(Entity e : nearby) {
            if(e instanceof LivingEntity && !e.equals(le)) {
                collide(le);
                return;
            }
        }

        for(int i = 0; i < 50; i++) {

            double yaw = Math.random() * Math.PI * 2;
            double pitch = Math.random() * Math.PI * 2;
            double mag = 1.5;

            Vector offset = velocity.clone().multiply(-1).normalize();

            Particle.FLAME.builder()
                    .location(le.getLocation().clone().add(Utility.RotationToVector(yaw, pitch, mag)))
                    .count(0)
                    .extra(Math.random() * 0.6 + 0.4)
                    .offset(offset.getX(), offset.getY(), offset.getZ())
                    .spawn();
            Particle.FLAME.builder()
                    .location(le.getLocation().clone().add(Utility.RotationToVector(yaw, pitch, mag * 0.3)))
                    .count(0)
                    .extra(Math.random() * 0.6 + 0.4)
                    .offset(offset.getX(), offset.getY(), offset.getZ())
                    .spawn();

            Particle.SMOKE.builder()
                    .location(le.getLocation().clone().add(Utility.RotationToVector(yaw, pitch, mag * 0.75)))
                    .count(3)
                    .offset(0.1, 0.1, 0.1)
                    .extra(0)
                    .spawn();
        }
        Particle.LAVA.builder()
                    .location(le.getLocation())
                    .count(3)
                    .spawn();

        le.setVelocity(velocity);


    }

    private void collide(LivingEntity le) {
        meteorTickProgress.remove(le);

        Collection<LivingEntity> entities = le.getWorld().getNearbyLivingEntities(le.getLocation(), 7);
        for(LivingEntity e : entities) {
            if(e.equals(le) || !e.isValid() || e.isDead()) continue;

            double d = e.getLocation().distance(le.getLocation());
            double damage = 25;
            int duration = 160;
            if(d > 2.5) {
                damage = 25 * Math.pow(0.6, d - 2.5);
                duration = (int)(160 * Math.pow(0.6, d - 2.5));
            }
            if(damage < 1) return;

            Vector dir = e.getLocation().clone().subtract(le.getLocation()).toVector().normalize();

            e.setVelocity(dir.multiply(3.25 * 1/Math.max(1,d)));
            e.setVelocity(new Vector(e.getVelocity().getX(), 1.5 / Math.max(1, Math.sqrt(d)), e.getVelocity().getZ()));

            damageEntity(le, e, damage, new Burn(duration, e.getUniqueId()));


        }

        Particle.EXPLOSION.builder()
                .location(le.getLocation())
                .offset(1.2,1.2,1.2)
                .count(60)
                .spawn();
        Particle.FLAME.builder()
                .location(le.getLocation())
                .offset(0.2,0.2,0.2)
                .count(100)
                .extra(0.3)
                .force(true)
                .spawn();
        Particle.LAVA.builder()
                .location(le.getLocation())
                .offset(2,2,2)
                .count(100)
                .extra(0.3)
                .force(true)
                .spawn();

        for(int i = 0; i < 100; i++) {
            final double YAW = Math.random() * 2 * Math.PI;
            final double MAG = Math.random() * 7;

            Location loc = le.getLocation().clone().add(Math.cos(YAW) * MAG, 0, Math.sin(YAW) * MAG);

            Particle.LARGE_SMOKE.builder()
                    .location(loc)
                    .count(0)
                    .offset(0,Math.random() * 0.03 + 0.01,0)
                    .extra(1)
                    .spawn();
        }
        double sphereYaw;
        double spherePitch;
        for(double i = 0; i < 360; i+= 0.2) {
            spherePitch = 2 * Math.PI / 360 * i;
            sphereYaw = i * 37;
            double mag = 0.8;

            Location spawnLoc = le.getLocation().clone().add(
                    Math.cos(sphereYaw) * Math.cos(spherePitch) * mag,
                    Math.sin(spherePitch) * mag,
                    Math.sin(sphereYaw) * Math.cos(spherePitch) * mag
            );
            Location move = le.getLocation().clone().add(
                    Math.cos(sphereYaw) * Math.cos(spherePitch) * (mag + 0.6),
                    Math.sin(spherePitch) * (mag + 0.6),
                    Math.sin(sphereYaw) * Math.cos(spherePitch) * (mag + 0.6)).subtract(spawnLoc);

            Particle.FLAME.builder()
                    .location(spawnLoc)
                    .count(0)
                    .offset(move.getX(),move.getY(),move.getZ())
                    .extra(1)
                    .force(true)
                    .spawn();

        }

        le.getWorld().playSound(le.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3.0f, 0.8f);
        le.getWorld().playSound(le.getLocation(), Sound.ENTITY_GENERIC_BURN, 3.0f, 0.8f);

    }

    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Meteor Surge"));
        item.lore(getLore());
        return item;
    }

    private static class MeteorPlayerData {
        private int ticks;
        private Vector descendDir;
        private boolean isDescending;

        private MeteorPlayerData() {descendDir =  new Vector();
        isDescending = false;}
    }
}
