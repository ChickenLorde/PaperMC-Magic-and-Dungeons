package me.oakyplugin.oakymagicplugin.projectiles;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.spells.ProjectileSpell;
import me.oakyplugin.oakymagicplugin.items.spells.shield.Shield;
import me.oakyplugin.oakymagicplugin.items.spells.shield.ShieldManager;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class LinearProjectile {
    private boolean isActive;
    private final OakyMagicPlugin plugin = OakyMagicPlugin.getPlugin();

    protected final ProjectileData stats;
    private final ProjectileSpell spell;

    private final LivingEntity owner;
    private Display visualDisplay;


    private Location pos;
    private Vector dir;

    int ticksActive;
    private int updateTicksActive;

    private final HashSet<EntityType> ignoreEntities = new HashSet<>();

    public LinearProjectile(ProjectileSpell spell, LivingEntity owner, Location start, Vector dir, ProjectileData stats) {
        this.pos = start;
        this.spell = spell;
        this.stats = stats;
        this.dir = dir;
        this.owner = owner;
        isActive = true;
        ticksActive = 0;
        updateTicksActive = 0;

        plugin.getProjectileManager().addProjectile(this);

    }
    public LinearProjectile(ProjectileSpell spell, LivingEntity owner, Location start, ProjectileData stats) { this(spell, owner, start, start.getDirection(), stats); }
    public LinearProjectile(ProjectileSpell spell, LivingEntity owner, ProjectileData stats) { this(spell, owner, owner.getEyeLocation(), owner.getEyeLocation().getDirection(), stats); }

    final void tick() {
        if(!isActive) return;
        if(ticksActive >= stats.getMaxTicksAlive()) {
            end();
            return;
        }

        onTick();

        ticksActive++;
    }
    protected void onTick() {
        Vector moveAmount = dir.clone().multiply(stats.getSpeed());

        if(!isActive) return;

        if(collisions(moveAmount)) {
            end();
            return;
        }


        for(int i = 0; i < stats.getUpdatesPerTick(); i++) {

            pos.add(moveAmount.clone().multiply(1.0/stats.getUpdatesPerTick()));
            spell.onProjectileTick(pos, this);
            updateTicksActive++;
        }
        if(visualDisplay != null)
            updateVisualDisplay();

    }
    private void updateVisualDisplay() {
        if(!visualDisplay.isValid()) {
            visualDisplay.spawnAt(pos);
        }
        else {
            visualDisplay.addScoreboardTag("removeAfterDisable");
            visualDisplay.teleport(pos);
        }
    }

    private boolean shieldCollisionCheck(Vector movement) {
        Vector nextPos = pos.clone().add(movement).toVector();
        Vector currentPos = pos.clone().toVector();

        ShieldManager shieldManager = plugin.getShieldManager();

        for(Shield shield : shieldManager.getShields()) {
            if(!shield.getOwner().getWorld().equals(getWorld())) continue;

            Vector normal = shield.getLocation().getDirection();

            double currentDot = currentPos.clone().subtract(shield.getLocation().toVector()).normalize().dot(normal);
            double nextDot = nextPos.clone().subtract(shield.getLocation().toVector()).normalize().dot(normal);

            if(currentDot / nextDot > 0) continue;


            Vector d = nextPos.clone().subtract(currentPos);

            double denom = d.dot(normal);
            if (Math.abs(denom) < 1e-6) return false;

            double t = shield.getLocation().toVector().clone().subtract(currentPos).dot(normal) / denom;

            if (t < 0 || t > 1) return false;
            Vector hit = currentPos.clone().add(d.multiply(t));
            double radius = shield.getRadius() * 0.5;
            if(hit.distanceSquared(shield.getLocation().toVector()) <= radius * radius) {
                Player p = shield.getOwner();
                PlayerData data = plugin.getPlayerManager().getPlayerData(p);

                double percentOfMana = stats.getDamage() * 2 / data.getStats().getTotalMaxMana();
                double exhaustion = Math.sqrt(stats.getDamage() * 2);

                data.getStats().removeMana(stats.getDamage() * 2);
                data.getStats().addExhaustion(exhaustion * percentOfMana * 10);
                end();
                return true;
            }
        }
        return false;
    }

    protected boolean collisions(Vector movement) {

        if(shieldCollisionCheck(movement))
            return true;

        BoundingBox check = new BoundingBox();
        check.shift(pos);
        if(stats.getCollisionSize() > 0)
            check.expand(stats.getCollisionSize() * 0.5);
        else
            check.expand(0.01,0.01,0.01);

        check.expandDirectional(movement);

        if(getWorld().hasCollisionsIn(check)) {
            return continuousCollisionCheck(movement);
        }

        Collection<Entity> entities = getWorld().getNearbyEntities(check);

        checkEntities: {
            if (!entities.isEmpty()) {
                if (entities.size() == 1 && entities.contains(owner)) break checkEntities;

                for (Entity entity : entities) {
                    if (!(entity instanceof LivingEntity livingEntity)) continue;
                    if (!(livingEntity == owner || livingEntity == visualDisplay || livingEntity.isDead() || !livingEntity.isValid())) {
                        if(!ignoreEntities.contains(livingEntity.getType()))
                            return continuousCollisionCheck(movement);
                    }
                }
            }
        }

        return false;
    }

    private boolean continuousCollisionCheck(Vector movement) {
        BoundingBox box = new BoundingBox();
        box.shift(pos);
        if(stats.getCollisionSize() > 0)
            box.expand(stats.getCollisionSize() * 0.5);
        else
            box.expand(0.01,0.01,0.01);

        Vector shiftAmount = dir.clone().multiply(0.1);

        for(int i = 0; i < Math.max(1,(int)Math.ceil(movement.length() * 10.0)); i++) {
            if(getWorld().hasCollisionsIn(box)) {
                pos.add(shiftAmount.clone().multiply(i));
                end();
                return true;
            }

            Collection<Entity> entities = getWorld().getNearbyEntities(box);

            checkEntities: {
                if (!entities.isEmpty()) {
                    if (entities.size() == 1 && entities.contains(owner)) break checkEntities;

                    for (Entity entity : entities) {
                        if (!(entity instanceof LivingEntity livingEntity)) continue;

                        if(ignoreEntities.contains(livingEntity.getType())) continue;

                        if (!(livingEntity == owner || livingEntity == visualDisplay || livingEntity.isDead() || !livingEntity.isValid())) {
                            pos.add(shiftAmount.clone().multiply(i));
                            spell.onEntity(owner, livingEntity, pos);
                            if(owner instanceof Player p)
                                p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 0.35f, 1.0f);
                            end();
                            return true;
                        }
                    }
                }
            }
            box.shift(shiftAmount);
        }

        return false;
    }

    public void setVisualDisplay(Display entity) {
        visualDisplay = entity;
    }

    protected void end() {
        if(visualDisplay != null)
            visualDisplay.remove();
        isActive = false;
        spell.onProjectileEnd(pos);
    }

    public World getWorld() {return pos.getWorld();}
    public boolean getIsActive() {return isActive;}
    public int getTicksActive() {return ticksActive;}
    public int getUpdateTicksActive() {return updateTicksActive;}
    public Vector getDir() {return dir;}
    public void ignoreEntityType(EntityType type) {
        ignoreEntities.add(type);
    }

    public Location getPos() {
        return pos;
    }

    public ProjectileData getStats() {
        return stats;
    }

    public ProjectileSpell getSpell() {
        return spell;
    }

    public LivingEntity getOwner() {return owner;}
}
