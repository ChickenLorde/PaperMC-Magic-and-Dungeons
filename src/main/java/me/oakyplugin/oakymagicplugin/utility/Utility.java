package me.oakyplugin.oakymagicplugin.utility;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.dungeons.rooms.CardinalDirection;
import me.oakyplugin.oakymagicplugin.items.spells.shield.Shield;
import me.oakyplugin.oakymagicplugin.items.spells.shield.ShieldManager;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;

import java.io.File;
import java.util.Collection;

public class Utility {

    public static boolean BlockShapeContains(Block block, Vector pos) {


        if(block.getType().isAir() || block.isPassable() || pos == null) return false;
        VoxelShape shape = block.getCollisionShape();

        for(BoundingBox box : shape.getBoundingBoxes()) {
            box.shift(block.getLocation());
            if (box.contains(pos)) return true;
        }
        return false;
    }

    public static BiPair<Double, Double> VectorToRotation(Vector vector) {
        double pitch = Math.asin(vector.getY());
        double yaw = Math.atan2(vector.getZ(),vector.getX());

        return new BiPair<>(yaw, pitch);
    }

    public static Vector RotationToVector(double yaw, double pitch, double magnitude) {
        return new Vector(Math.cos(yaw) * Math.cos(pitch),
                Math.sin(pitch),
                Math.sin(yaw) * Math.cos(pitch)).multiply(magnitude);
    }

    public static Vector RotateAroundVector(Vector v, double yaw, double pitch, double magnitude) {
        Vector right;
        if(Math.abs(v.getY()) < 0.999)
            right = v.getCrossProduct(new Vector(0,1,0)).normalize();
        else
            right = v.getCrossProduct(new Vector(1,0,0)).normalize();

        Vector up = right.getCrossProduct(v).normalize();
        Vector ring = right.clone().multiply(Math.cos(yaw))
                .add(
                        up.clone().multiply(Math.sin(yaw))
                ).normalize();

        return v.clone().add(
                ring.multiply(Math.cos(pitch)).add(
                        v.clone().multiply(Math.sin(pitch))
                )).multiply(magnitude);
    }

    public static boolean shieldCollisionCheck(Location pos, Vector movement, double damage) {
        OakyMagicPlugin plugin = OakyMagicPlugin.getPlugin();
        Vector nextPos = pos.clone().add(movement).toVector();
        Vector currentPos = pos.clone().toVector();

        ShieldManager shieldManager = plugin.getShieldManager();

        for(Shield shield : shieldManager.getShields()) {
            if(!shield.getOwner().getWorld().equals(pos.getWorld())) continue;

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

                double percentOfMana = damage * 2 / data.getStats().getTotalMaxMana();
                double exhaustion = Math.sqrt(damage * 2);

                data.getStats().removeMana(damage * 2);
                data.getStats().addExhaustion(exhaustion * percentOfMana * 10);
                return true;
            }
        }
        return false;
    }


    public static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }

        file.delete();
    }

    public static CardinalDirection entityFacingToCardinal(LivingEntity e) {
        BlockFace face = e.getFacing();
        return switch(face) {
            case NORTH -> CardinalDirection.NORTH;
            case EAST -> CardinalDirection.EAST;
            case WEST -> CardinalDirection.WEST;
            default -> CardinalDirection.SOUTH;
        };
    }

    public static Vector floorVector(Vector vec) {
        return new Vector(Math.floor(vec.getX()),Math.floor(vec.getY()),Math.floor(vec.getZ()));
    }

    public static Vector centerVectorXZ(Vector vec) {
        double x = vec.getX();
        double z = vec.getZ();

        x -= (x % 1.0);
        z -= (z % 1.0);

        return new Vector(x,vec.getY(),z);
    }

    //public static Vector

    public static Vector flatRotateVector(Vector vector, FlatRotation rotation) {
        return switch(rotation) {
            case NONE -> vector.clone();
            case CW_90 -> new Vector(vector.getZ(),vector.getY(),-vector.getX());
            case FLIP_180 -> new Vector(-vector.getX(), vector.getY(), -vector.getZ());
            case CCW_90 -> new Vector(-vector.getZ(), vector.getY(), vector.getX());
        };
    }


    public static double rad2Deg(double a) {
        return a * 180 / Math.PI;
    }
    public static double deg2Rad(double d) {
        return d * Math.PI / 180;
    }

    public enum FlatRotation {
        NONE,
        CW_90,
        CCW_90,
        FLIP_180

    }

    public static  <T extends Entity>  T getNearestEntity(Collection<T> entities, Location location) {
        T closestEntity = null;
        double closest = Double.MAX_VALUE;
        for(T e : entities) {
            double distance = e.getLocation().distanceSquared(location);
            if(distance < closest) {
                closest = distance;
                closestEntity = e;
            }
        }
        return closestEntity;
    }
}
