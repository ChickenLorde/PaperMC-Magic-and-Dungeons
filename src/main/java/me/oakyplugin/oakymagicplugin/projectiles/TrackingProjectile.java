package me.oakyplugin.oakymagicplugin.projectiles;

import me.oakyplugin.oakymagicplugin.items.spells.ProjectileSpell;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class TrackingProjectile extends LinearProjectile {

    private final TrackingProjectileData trackingData;
    private LivingEntity target;
    private Vector velocity;
    private Vector angle;

    private boolean initialized;
    public TrackingProjectile(ProjectileSpell spell, LivingEntity owner, Location start, Vector dir, ProjectileData stats, TrackingProjectileData trackingData) {
        super(spell, owner, start, dir, stats);
        initialized = false;
        this.trackingData = trackingData;
    }

    public TrackingProjectile(ProjectileSpell spell, LivingEntity owner, Location start, ProjectileData stats, TrackingProjectileData trackingData) {
        this(spell, owner, start, start.getDirection(), stats, trackingData);
    }

    public TrackingProjectile(ProjectileSpell spell, LivingEntity owner, ProjectileData stats, TrackingProjectileData trackingData) {
        this(spell, owner, owner.getLocation(), stats, trackingData);
    }

    @Override
    protected void onTick() {
        if( !getIsActive() || target == null || !target.isValid() || !target.getLocation().getWorld().equals(getPos().getWorld())) {
            if(getIsActive())
                end();
            return;
        }



        int updates = stats.getUpdatesPerTick();
        double inverse = 1.0 / updates;

        double distanceToTarget = target.getLocation().distance(getPos().clone().add(velocity));

        Location targetLoc = target.getBoundingBox().getCenter().clone().add(target.getVelocity().clone().multiply(distanceToTarget)).toLocation(target.getWorld());

        for(int i = 0; i < updates; i++) {
            double trackingDistance = trackingData.getTrackingDistance();

            if(trackingDistance == -1 || distanceToTarget < trackingDistance) {
                Vector targetAngle = targetLoc.clone().subtract(getPos().clone().add(velocity)).toVector().normalize();
                Vector turnAmount = targetAngle.subtract(angle).normalize().multiply(trackingData.getTurnSpeed());

                angle.add(turnAmount.multiply(inverse)).normalize();
            }

            velocity.add(angle.clone().multiply(trackingData.getAccelMagnitude() * inverse));
            velocity.multiply(Math.pow(1 - trackingData.getDragCoeff(), inverse));


            if(collisions(velocity)) {
                end();
                return;
            }
            getPos().add(velocity.clone().multiply(inverse));

            getSpell().onProjectileTick(getPos(), this);
        }
    }

    public Target setInitialValues() {
        if(initialized) {
            throw new RuntimeException("Cannot set initial values after initialized");
        }
        initialized = true;
        return new TrackingProjectileValues(this);

    }

    public interface Target { Velocity target(LivingEntity x); }
    public interface Velocity { Angle velocity(Vector x); }
    public interface Angle { SetValues angle(Vector x); }
    public interface SetValues { TrackingProjectile setValues(); }

    public static final class TrackingProjectileValues implements Target, Velocity, Angle, SetValues{

        private TrackingProjectile projectile;
        private LivingEntity target;
        private Vector velocity;
        private Vector angle;

        private TrackingProjectileValues(TrackingProjectile projectile) { this.projectile = projectile;}


        @Override
        public SetValues angle(Vector x) {
            angle = x;
            return this;
        }

        @Override
        public TrackingProjectile setValues() {
            projectile.target = target;
            projectile.velocity = velocity;
            projectile.angle = angle;
            return projectile;
        }

        @Override
        public Velocity target(LivingEntity x) {
            target = x;
            return this;
        }

        @Override
        public Angle velocity(Vector x) {
            velocity = x;
            return this;
        }
    }
}
