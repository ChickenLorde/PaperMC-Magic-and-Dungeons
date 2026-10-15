package me.oakyplugin.oakymagicplugin.random_events.events;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.random_events.RandomEvent;
import me.oakyplugin.oakymagicplugin.random_events.event_entities.FallingStarEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Interaction;

import java.util.HashMap;
import java.util.HashSet;

public class FallingStarEvent extends RandomEvent {

    World world = Bukkit.getWorld("world");
    public FallingStarEvent() {
        super(40, 0.00033);

    }

    @Override
    public boolean precondition() {
        switch(world.getMoonPhase()) {
            case FULL_MOON: setOccurChanceMult(1.4); break;
            case WANING_GIBBOUS, WAXING_GIBBOUS: setOccurChanceMult(1.2); break;
            case LAST_QUARTER, FIRST_QUARTER: setOccurChanceMult(1.0); break;
            case WANING_CRESCENT, WAXING_CRESCENT: setOccurChanceMult(0.85); break;
            case NEW_MOON: setOccurChanceMult(0.7); break;
        }

        return world != null && world.getTime() > 14000 && world.getTime() < 23000;
    }

    @Override
    protected void onTryToOccur() {
        //Bukkit.broadcast(Component.text("Tried to spawn star"));

        Chunk[] chunks = world.getLoadedChunks();

        for(Chunk chunk : chunks) {
            if(rollForOccurrence()) {
                //Bukkit.broadcast(Component.text("Spawned star"));
                new FallingStarEntity(OakyMagicPlugin.getPlugin().getRandomEventsManager(), chunk);
            }
        }

    }
}
