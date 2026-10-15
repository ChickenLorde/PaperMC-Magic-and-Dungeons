package me.oakyplugin.oakymagicplugin.entity.bosses.elite_necromancer;

import com.destroystokyo.paper.ParticleBuilder;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.bosses.BossAttack;
import me.oakyplugin.oakymagicplugin.entity.bosses.BossEntity;
import me.oakyplugin.oakymagicplugin.items.spells.WitheringWisp;
import me.oakyplugin.oakymagicplugin.projectiles.TrackingProjectile;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class SummonWispAttack extends BossAttack {

    private final OakyMagicPlugin plugin = OakyMagicPlugin.getPlugin();
    private final Random rand = new Random();

    private final NamespacedKey modifier = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "elite_necromancer.summon_wisp_modifier");

    public SummonWispAttack(BossEntity boss) {
        super(boss,60, 40, 30, 200);
    }


    @Override
    protected void onAttack() {
        WitheringWisp wisp = plugin.getItemManager().getCustomItem(WitheringWisp.class);
        LivingEntity bossEntity = boss.getEntity();

        World world = bossEntity.getWorld();

        Location bossLocation = bossEntity.getLocation();
        Collection<LivingEntity> entities = world.getNearbyLivingEntities(bossLocation, 32);

        if(entities.isEmpty()) return;

        double farthestDistance = Double.MIN_VALUE;
        LivingEntity farthest = null;
        for(LivingEntity e : entities) {
            if(e.equals(bossEntity) || !(e instanceof Player p)) continue;
            if(p.getGameMode().isInvulnerable() || !bossEntity.hasLineOfSight(p)) continue;
            double distance = bossLocation.distanceSquared(e.getLocation());
            if(distance > farthestDistance) {
                farthestDistance = distance;
                farthest = e;
            }
        }
        if(farthest == null) return;

        double yaw = rand.nextDouble(Math.PI * 2);
        double pitch = rand.nextDouble(Math.PI * 0.25, Math.PI * 0.5);
        Vector velocity = Utility.RotationToVector(yaw, pitch, 1);
        TrackingProjectile p = wisp.castTrackingProjectile(bossEntity, farthest, bossEntity.getEyeLocation(), velocity, velocity.clone().normalize());
        p.ignoreEntityType(EntityType.ZOMBIE);
        p.ignoreEntityType(EntityType.SKELETON);
    }

    @Override
    protected void chargeTick() {
        Location bossLocation = boss.getEntity().getLocation();
        for(int i = 0; i < 24; i++) {
            double yaw = Math.PI / 12 * i;
            double pitch = Utility.deg2Rad(20);
            Vector velocity = Utility.RotationToVector(yaw, pitch, 0.15);

            Particle.SOUL.builder()
                    .location(bossLocation)
                    .count(0)
                    .offset(velocity.getX(), velocity.getY(), velocity.getZ())
                    .spawn();
        }
    }

    @Override
    protected void onAttackStart() {
        LivingEntity bossEntity = boss.getEntity();
        Location loc = bossEntity.getLocation();

        Collection<LivingEntity> entities = loc.getNearbyLivingEntities(6, x -> x instanceof Player);
        if(!entities.isEmpty())
            teleportBossAway(Utility.getNearestEntity(entities, loc));


        bossEntity.setVelocity(new Vector(0,0,0));

        if(bossEntity instanceof Mob m) {
            m.setAware(false);
        }

        AttributeInstance mvmt = bossEntity.getAttribute(Attribute.MOVEMENT_SPEED);
        if(mvmt != null) {
            if(mvmt.getModifier(modifier) == null)
                mvmt.addModifier(new AttributeModifier(modifier, -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        }
    }

    private void teleportBossAway(LivingEntity target) {
        double angle = 0;
        Random rand = new Random();
        final double maxRadius = 16;
        final double minRadius = 8;
        final double radius = rand.nextDouble(minRadius,maxRadius);
        final double radiusSquared = Math.pow(radius, 2);
        LivingEntity bossEntity = boss.getEntity();
        BoundingBox bossBox = bossEntity.getBoundingBox().clone();

        TeleportPoint bestTeleportPoint = null;

        point:
        while(angle < Math.PI * 2) {
            angle += rand.nextDouble(Math.PI / 36, Math.PI / 24);
            Location pointLoc = target.getLocation().clone().add(Utility.RotationToVector(angle, 0, radius));
            if(pointLoc.distanceSquared(bossEntity.getLocation()) > radiusSquared) continue;


            if(!pointLoc.getBlock().isPassable())
                for(int i = 0; i < radius; i++) {
                    if(pointLoc.getBlock().isPassable() && !pointLoc.getBlock().isLiquid()) {
                        break;
                    }
                    pointLoc.add(0,1,0);
                    if(i == radius) continue point;
                }
            else
                for(int i = 0; i < radius; i++) {
                    if(!pointLoc.getBlock().isPassable() && !pointLoc.getBlock().isLiquid()) {
                        break;
                    }
                    pointLoc.add(0,-1,0);
                    if(i == radius) continue point;
                }

            if(!bossEntity.hasLineOfSight(pointLoc)) continue;

            double distanceToTarget = pointLoc.distance(target.getLocation());

            RayTraceResult res = pointLoc.getWorld().rayTraceBlocks(pointLoc, target.getLocation().clone().subtract(pointLoc).toVector().normalize(), distanceToTarget);
            if(res != null && res.getHitBlock() != null && !res.getHitBlock().isPassable()) continue;
            //Make sure point is valid (no lava or any hazards) then find the highest point up to 16 blocks higher
            //Continue if point is more than radius blocks from the boss's original location
            //if the point doesn't have line of sight to target, continue as well
            //Get distance to the closest Player, not counting the target entity

            Collection<LivingEntity> entities = pointLoc.getNearbyLivingEntities(radius, x -> x instanceof Player && !x.equals(target));
            LivingEntity closest = Utility.getNearestEntity(entities, pointLoc);

            if(bestTeleportPoint == null || closest == null || distanceToTarget > bestTeleportPoint.distanceToClosestPlayer)
                bestTeleportPoint = new TeleportPoint(pointLoc, closest == null ? radiusSquared : pointLoc.distanceSquared(closest.getLocation()));

        }

        if(bestTeleportPoint != null) {
            ParticleBuilder soul = Particle.SOUL.builder()
                    .location(bossEntity.getBoundingBox().getCenter().toLocation(bossEntity.getWorld()))
                    .count(100)
                    .extra(0.12)
                    .spawn();
            soul.location(bestTeleportPoint.location.clone().add(0,bossEntity.getHeight() * 0.5,0)).spawn();
            ParticleBuilder flame = Particle.SOUL_FIRE_FLAME.builder()
                    .location(bossEntity.getBoundingBox().getCenter().toLocation(bossEntity.getWorld()))
                    .count(100)
                    .extra(0.12)
                    .spawn();
            flame.location(bestTeleportPoint.location.clone().add(0,bossEntity.getHeight() * 0.5,0));
            bossEntity.teleport(bestTeleportPoint.location);
        }

        //Pick the point with the greatest distance to the nearest player
        //teleport there
    }

    @Override
    protected void onAttackEnd() {
        LivingEntity e = boss.getEntity();

        if(e instanceof Mob m) {
            m.setAware(true);
        }

        AttributeInstance mvmt = e.getAttribute(Attribute.MOVEMENT_SPEED);
        if(mvmt != null) {
            if(mvmt.getModifier(modifier) != null)
                mvmt.removeModifier(modifier);
        }
    }

    @Override
    protected boolean precondition() {
        Location bossLoc = boss.getEntity().getLocation();
        Collection<LivingEntity> entities = bossLoc.getNearbyLivingEntities(32);
        for(LivingEntity e : entities) {
            if(!(e instanceof Player p)) continue;
            if(p.getGameMode().isInvulnerable()) continue;

            if(p.getLocation().distanceSquared(bossLoc) <= 1024 && boss.getEntity().hasLineOfSight(p)) return true;
        }
        return false;
    }

    private record TeleportPoint(Location location, double distanceToClosestPlayer) {
    }
}
