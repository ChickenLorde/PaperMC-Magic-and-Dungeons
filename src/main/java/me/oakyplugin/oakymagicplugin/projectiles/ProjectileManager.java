package me.oakyplugin.oakymagicplugin.projectiles;

import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ProjectileManager {
    private final Set<LinearProjectile> projectiles = new HashSet<>();

    public void tick() {
        for(LinearProjectile projectile : new HashSet<>(projectiles)) {
            projectile.tick();
            if(!projectile.getIsActive()) projectiles.remove(projectile);
        }
    }
    public void addProjectile(LinearProjectile projectile) {projectiles.add(projectile);}

    public Collection<LinearProjectile> getOwnerProjectiles(LivingEntity e) {
        HashSet<LinearProjectile> owned = new HashSet<>();

        for(LinearProjectile projectile : projectiles) {
            if(projectile.getOwner().equals(e))
                owned.add(projectile);
        }
        return owned;
    }

}
