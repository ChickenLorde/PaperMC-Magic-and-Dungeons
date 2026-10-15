package me.oakyplugin.oakymagicplugin.projectiles;

import org.bukkit.entity.Display;

public class ProjectileData {
    private double damage;
    private double speed ;
    private int maxTicksAlive;
    private double collisionSize;
    private int updatesPerTick;
    private Display visualDisplay;

    public ProjectileData(double damage, double speed, int maxTicksAlive, double collisionSize, int updatesPerTick) {
        this.damage = damage;
        this.speed = speed;
        this.maxTicksAlive = maxTicksAlive;
        this.collisionSize = collisionSize;
        this.updatesPerTick = updatesPerTick;
    }

    public double getDamage() {return damage;}
    public double getSpeed() {return speed;}
    public int getMaxTicksAlive() {return maxTicksAlive;}
    public double getCollisionSize() {return collisionSize;}
    public int getUpdatesPerTick() {return updatesPerTick;}

    //region step-builder
    public static Damage builder() {return new ProjectileDataBuilder(); }
    public interface Damage { Speed damage(double x); }
    public interface Speed { MaxTicksAlive speed(double x); }
    public interface MaxTicksAlive { CollisionSize maxTicksAlive(int x); }
    public interface CollisionSize { UpdatesPerTick collisionSize(double x); }
    public interface UpdatesPerTick { Build updatesPerTick(int x); }
    public interface Build { ProjectileData build(); }
    //endregion
    public static final class ProjectileDataBuilder implements Damage, Speed, MaxTicksAlive, CollisionSize, UpdatesPerTick, Build {
        private double damage;
        private double speed ;
        private int maxTicksAlive;
        private double collisionSize;
        private int updatesPerTick ;

        public ProjectileData build() {
            return new ProjectileData(damage, speed, maxTicksAlive, collisionSize, updatesPerTick);
        }

        public UpdatesPerTick collisionSize(double x) {
            collisionSize = x;
            return this;
        }
        public Speed damage(double x) {
            damage = x;
            return this;
        }
        public CollisionSize maxTicksAlive(int x) {
            maxTicksAlive = x;
            return this;
        }
        public MaxTicksAlive speed(double x) {
            speed = x;
            return this;
        }
        public Build updatesPerTick(int x) {
            updatesPerTick = x;
            return this;
        }

    }
}
