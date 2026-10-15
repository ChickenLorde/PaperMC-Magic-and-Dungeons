package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.entity.TeleportFlag;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Blink extends Spell {

    private final double TELEPORT_DISTANCE;

    public Blink() {
        super(new SpellStats(20, 30, 5));

        TELEPORT_DISTANCE = 6;

        addStatLore("Distance",6,"yellow",0);
        buildLore("\"Teleports your body a short distance, but can get quite tiring after repeated use\"");
    }


    @Override
    public boolean cast(LivingEntity caster, Location location) {
        double distanceTraveled = 0;
        Location startLoc = caster.getEyeLocation().clone();
        Location currentLoc = caster.getEyeLocation().clone();
        Vector teleMove = caster.getEyeLocation().getDirection().clone().multiply(0.1);

        Particle.PORTAL.builder()
                .location(caster.getBoundingBox().getCenter().toLocation(caster.getWorld()))
                .count(250)
                .extra(1)
                .offset(caster.getWidth() / 6.0, caster.getHeight() / 6.0, caster.getWidth() / 6.0)
                .spawn();

        while(distanceTraveled < TELEPORT_DISTANCE) {
            Location shift = currentLoc.clone().add(teleMove);
            Block block = shift.getBlock();

            boolean hitBlock = Utility.BlockShapeContains(block, shift.toVector());
            if(hitBlock) {
                currentLoc.set(currentLoc.getBlockX() + 0.5, currentLoc.getBlockY() + (caster.getEyeLocation().getY() - caster.getLocation().getY()) , currentLoc.getBlockZ() + 0.5);
                break;
            }
            currentLoc.add(teleMove);

            distanceTraveled = currentLoc.distance(startLoc);
        }

        Location teleportLocation = currentLoc.subtract(0, caster.getEyeLocation().getY() - caster.getLocation().getY(),0);

        Block blockBelow = teleportLocation.clone().subtract(0,0.5,0).getBlock();
        if(!blockBelow.isPassable()) {

            while(!Utility.BlockShapeContains(blockBelow, teleportLocation.toVector()))
                teleportLocation.subtract(0,0.1,0);
        }


        while(Utility.BlockShapeContains(teleportLocation.getBlock(), teleportLocation.toVector())) {
            teleportLocation.add(0,0.1,0);
        }
        caster.teleport(teleportLocation,
                TeleportFlag.Relative.VELOCITY_X,
                TeleportFlag.Relative.VELOCITY_Y,
                TeleportFlag.Relative.VELOCITY_Z);
        caster.setFallDistance(0);
        caster.getWorld().playSound(teleportLocation, Sound.ENTITY_PLAYER_TELEPORT, 1.0f, 1.0f);

        Particle.REVERSE_PORTAL.builder()
                .location(caster.getBoundingBox().getCenter().toLocation(caster.getWorld()))
                .count(400)
                .extra(0.2)
                .offset(caster.getWidth() / 6.0, caster.getHeight() / 6.0, caster.getWidth() / 6.0)
                .spawn();

        Location particleLoc = caster.getEyeLocation().clone();

        int max = 80;
        while(particleLoc.distance(startLoc) > 0.2 && max > 0) {
            Vector moveDir = startLoc.clone().subtract(particleLoc).toVector().normalize().multiply(0.1);
            particleLoc.add(moveDir);
            Particle.DUST.builder()
                    .location(particleLoc)
                    .count(2)
                    .offset(0.04,0.04,0.04)
                    .extra(0)
                    .color(Color.fromRGB(255,50,255))
                    .spawn();
            max--;
        }

        return true;
    }

    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Blink"));

        item.lore(getLore());
        return item;
    }
}
