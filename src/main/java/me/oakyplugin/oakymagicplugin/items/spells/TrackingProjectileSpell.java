package me.oakyplugin.oakymagicplugin.items.spells;

import me.oakyplugin.oakymagicplugin.projectiles.LinearProjectile;
import me.oakyplugin.oakymagicplugin.projectiles.ProjectileData;
import me.oakyplugin.oakymagicplugin.projectiles.TrackingProjectile;
import me.oakyplugin.oakymagicplugin.projectiles.TrackingProjectileData;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public abstract class TrackingProjectileSpell extends ProjectileSpell {
    private final TrackingProjectileData trackingData;
    public TrackingProjectileSpell(SpellStats stats, ProjectileData projectileData, TrackingProjectileData trackingData) {
        super(stats, projectileData);
        this.trackingData = trackingData;
    }

    public final TrackingProjectileData getTrackingData() {return trackingData;}

    @Override
    public TrackingProjectile createProjectile(LivingEntity owner, Location pos, Vector dir) {
        return new TrackingProjectile(this, owner, pos, dir, getProjectileStats(), trackingData);
    }

    public TrackingProjectile castTrackingProjectile(LivingEntity caster, LivingEntity target, Location location) { return null; }
}
