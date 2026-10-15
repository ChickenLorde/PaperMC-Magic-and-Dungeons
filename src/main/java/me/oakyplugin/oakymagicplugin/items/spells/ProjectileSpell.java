package me.oakyplugin.oakymagicplugin.items.spells;

import me.oakyplugin.oakymagicplugin.projectiles.LinearProjectile;
import me.oakyplugin.oakymagicplugin.projectiles.ProjectileData;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.Collection;

public abstract class ProjectileSpell extends Spell {
    private final ProjectileData projectileData;


    public ProjectileSpell(SpellStats stats, ProjectileData projectileData) {
        super(stats);
        this.projectileData = projectileData;
    }
    public abstract void onProjectileTick(Location location, LinearProjectile projectile);
    public abstract void onProjectileEnd(Location location);
    public ProjectileData getProjectileStats() {return projectileData;}

    public LinearProjectile createProjectile(LivingEntity owner, Location pos, Vector dir) {
        return new LinearProjectile(this, owner, pos, dir, projectileData);
    }

    public LinearProjectile castProjectile(LivingEntity caster, Location location) { return null; }
}
