package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.spells.magic_circles.ExplosionMagicCircle;
import me.oakyplugin.oakymagicplugin.items.spells.magic_circles.MagicCircle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Explosion extends Spell {

    private final HashMap<Player , Location> explosionPositionTrackers;

    public Explosion() {
        super(new SpellStats(500, 20,480, 250));
        explosionPositionTrackers = new HashMap<>();

        addStatLore("Damage","A LOT","red");
        buildLore("\"Ultimate destructive magic.\"");
    }

    @Override
    public boolean cast(LivingEntity caster, Location location) {


        Location explosionLoc = explosionPositionTrackers.get(caster);
        new BukkitRunnable() {//p.sendMessage("Explosion Cast");
            int j = 0;
            int power = 70;
            Random rand = new Random();
            @Override
            public void run() {
                if(j >= power) {
                    cancel();
                    return;
                }

                Particle beam = Particle.FLAME.builder()
                        .location(explosionLoc)
                        .force(true)
                        .particle();
                Bukkit.getScheduler().runTaskAsynchronously(OakyMagicPlugin.getPlugin(), () -> {
                    for (int k = 0; k < 300; k++) {

                        double yaw = Math.random() * Math.PI * 2;
                        double mag = Math.min(3, rand.nextGaussian() * 0.75);

                        Location shifted = explosionLoc.clone().add(Math.cos(yaw) * mag, k / 2.0, Math.sin(yaw) * mag);

                        beam.builder()
                                .location(shifted)
                                .count(0)
                                .offset(0, -1000000, 0)
                                .extra(0.00001 * (Math.random() * 0.3 + 0.7))
                                .spawn();
                        beam.builder()
                                .location(shifted)
                                .count(1)
                                .extra(0.25)
                                .spawn();
                    }
                });

                for (int i = 0; i < j/20 + 4; i++) {
                    double yaw = Math.random() * Math.PI * 2;
                    double pitch = Math.random() * Math.PI * 2;
                    double mag = Math.random() + j / 3.1;

                    double y = Math.clamp(Math.sin(pitch) * mag, -power /11.0,power /11.0);

                    Location shifted = explosionLoc.clone().add(Math.cos(yaw) * Math.cos(pitch) * mag, y, Math.sin(yaw) * Math.cos(pitch) * mag);

                    shifted.createExplosion(caster, j/20f + 10, j > power / 2, true);
                }
                j++;
            }
        }.runTaskTimer(OakyMagicPlugin.getPlugin(),0L,2L);



        return true;
    }

    @Override
    public boolean onChargeStart(Player p, Location location) {
        Location loc = null;
        RayTraceResult res = p.rayTraceBlocks(150);
        if(res != null) {
            Block b = res.getHitBlock();
                    if(b != null) {
                        loc = b.getLocation();
                        explosionPositionTrackers.put(p, loc);
                    }
        }
        //p.sendMessage("Explosion Start");
        if(loc == null) {
            if(OakyMagicPlugin.getPlugin().getPlayerManager().getPlayerData(p).getRightClickDownTime() > 1) return false;
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>You must target a block!"));
            OakyMagicPlugin.getPlugin().getItemManager().resetPlayerCharges(p);
            return false;
        }
        return true;
    }

    @Override
    public void chargeTick(Player p, Location location, int chargeTicks) {
        if(chargeTicks == 40)
            p.chat("Darkness blacker than black and darker than dark");
        if(chargeTicks == 100)
            p.chat("I beseech thee, combine with my deep crimson");
        if(chargeTicks == 160)
            p.chat("The time of awakening cometh");
        if(chargeTicks == 200)
            p.chat("Justice, fallen upon the infallible boundary");
        if(chargeTicks == 250) {
            ExplosionMagicCircle a = new ExplosionMagicCircle(p, explosionPositionTrackers.get(p).clone().add(0,12,0), this, 18,0);
            ExplosionMagicCircle b = new ExplosionMagicCircle(p, explosionPositionTrackers.get(p).clone().add(0,18,0), this, 13,3);
            ExplosionMagicCircle c = new ExplosionMagicCircle(p, explosionPositionTrackers.get(p).clone().add(0,22,0), this, 24,6);
            ExplosionMagicCircle d = new ExplosionMagicCircle(p, explosionPositionTrackers.get(p).clone().add(0,30,0), this, 18,9);
            ExplosionMagicCircle e = new ExplosionMagicCircle(p, explosionPositionTrackers.get(p).clone().add(0,38,0), this, 13,12);
            ExplosionMagicCircle f = new ExplosionMagicCircle(p, explosionPositionTrackers.get(p).clone().add(0,50,0), this, 48,22);
            p.chat("appear now as an intangible distortions!");
        }
        if(chargeTicks == 295)
            p.chat("I desire for my torrent of power a destructive force:");
        if(chargeTicks == 365)
            p.chat("a destructive force without equal!");
        if(chargeTicks == 405)
            p.chat("Return all creation to cinders");
        if(chargeTicks == 460)
            p.chat("EXPLOOOOOSION!");

        if(chargeTicks == 470) {
            HashSet<MagicCircle> circles = OakyMagicPlugin.getPlugin().getMagicCircleManager().getPlayerMagicCircles(p);
            for(MagicCircle circle : circles) {
                if(circle.isDespawning()) continue;
                circle.startDespawn();
            }
        }

        Location loc = explosionPositionTrackers.get(p);
        if(loc != null) {
            Particle.FLAME.builder()
                    .location(explosionPositionTrackers.get(p).clone().add(0,0.5,0))
                    .count(4 + (int)(chargeTicks * 0.02))
                    .extra(chargeTicks * 0.0011 + 0.01)
                    .spawn();

            if(chargeTicks > 365) {
                for(int i = 0; i < 200; i ++)
                    Particle.FLAME.builder()
                            .location(explosionPositionTrackers.get(p).clone().add(0,i * 0.5,0))
                            .count((chargeTicks - 365)/50)
                            .extra((chargeTicks - 365)/200.0)
                            .offset(0.1,0,0.1)
                            .force(true)
                            .spawn();

            }

            Random rand = new Random();
            for (int i = 0; i < chargeTicks / 4 + 1; i++) {
                double yaw = Math.random() * Math.PI * 2;
                double mag = Math.min(36,4 + rand.nextGaussian() * 32);
                Particle.FLAME.builder()
                        .location(explosionPositionTrackers.get(p).clone().add(Math.cos(yaw) * mag, 0.5, Math.sin(yaw) * mag))
                        .count(0)
                        .offset(Math.random() * -0.02 + 0.01, Math.random() * 0.2 * chargeTicks / 40 + 0.0005, Math.random() * -0.02 + 0.01)
                        .force(true)
                        .spawn();
            }
        }
    }

    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Explosion"));

        item.lore(getLore());

        return item;
    }

    @Override
    public void onResetCharge(Player p) {
        //p.sendMessage("Explosion Canceled");
        explosionPositionTrackers.remove(p);
    }

    @Override
    public void onEntity(LivingEntity cause, LivingEntity target, Location location) {

    }
}
