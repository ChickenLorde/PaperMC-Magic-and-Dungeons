package me.oakyplugin.oakymagicplugin.random_events.event_entities;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.materials.ManaPowder;
import me.oakyplugin.oakymagicplugin.random_events.RandomEventEntity;
import me.oakyplugin.oakymagicplugin.random_events.RandomEventsManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class FallingStarEntity extends RandomEventEntity {

    private Interaction interaction;

    private Location pos;
    private final double speed;
    private final Vector dir;

    private final int spawnDelay;

    private boolean isLanded;

    public FallingStarEntity(RandomEventsManager manager, Chunk chunk) {
        super(manager);

        isLanded = false;

        double x = ThreadLocalRandom.current().nextDouble(0, 16);
        double z = ThreadLocalRandom.current().nextDouble(0, 16);
        double y = chunk.getWorld().getHighestBlockYAt((int)x + chunk.getX() * 16, (int)z + chunk.getZ() * 16);
        pos = new Location(chunk.getWorld(), x + chunk.getX() * 16,y + 180,z  + chunk.getZ() * 16);

        spawnDelay = ThreadLocalRandom.current().nextInt(0,50);

        double yaw = Math.toRadians(Math.random() * 360);
        double pitch = Math.toRadians(Math.random() * 70 + 235);

        dir = new Vector();
        dir.setX(Math.cos(pitch) * Math.cos(yaw));
        dir.setZ(Math.cos(pitch) * Math.sin(yaw));
        dir.setY(Math.sin(pitch));
        dir.normalize();

        speed = ThreadLocalRandom.current().nextDouble(0.85,1.5);
    }

    @Override
    public void onTick() {
        if(getTicksAlive() < spawnDelay) return;

        if(getTicksAlive() > 900 + spawnDelay || pos.getWorld().isDayTime()) {
            isAlive = false;
            onRemove();
            return;
        }

        if(!isLanded) {
            pos.add(dir.clone().multiply(speed));
        }

        if(!isLanded && !pos.getBlock().isPassable()) {
            isLanded = true;

            while(!pos.getBlock().isPassable())
                pos.add(0,0.1,0);

            getManager().linkInteractionToStar(this);
            Particle.FIREWORK.builder()
                    .location(pos)
                    .count(75)
                    .extra(0.2)
                    .spawn();
        }

        if(!isLanded)
            Particle.FIREWORK.builder()
                    .location(pos)
                    .count(4)
                    .extra(0.04)
                    .receivers(256)
                    .force(true)
                    .spawn();
        else
            Particle.FIREWORK.builder()
                    .location(pos)
                    .count(4)
                    .extra(0.06)
                    .receivers(256)
                    .force(true)
                    .spawn();
    }

    @Override
    public void onRemove() {
        Particle.FIREWORK.builder()
                .location(pos)
                .count(75)
                .extra(0.1)
                .spawn();
    }

    public void linkInteraction(Interaction interaction) {
        if(this.interaction != null) {
            this.interaction.remove();
        }
        this.interaction = interaction;
    }

    public void onInteract(Player p) {
        Interaction interaction = getManager().getStarInteractionPairs().getFromLeft(this);
        if(interaction == null) return;
        interaction.remove();

        //getManager().getStarInteractionPairs().removeBiPairA(this);
        isAlive = false;
        onRemove();

        int rand = (int)(Math.random() * 2);

        if(rand == 0) {
            rand = (int)(Math.random() * 2 + 1);
            p.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray>You try to pick up the delicate star, but it collapses into a fine powder: <aqua>+"+rand+" mana powder"));
            OakyMagicPlugin.getPlugin().getItemManager().getCustomItem(ManaPowder.class).give(rand, p);
        }
        else if(rand == 1) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray>Upon touching it, the star absorbs into your body: <aqua>+25 mana"));
            OakyMagicPlugin.getPlugin().getPlayerManager().getPlayerData(p).getStats().addMana(25);
        }

    }

    public Location getLocation() {return pos;}
}
