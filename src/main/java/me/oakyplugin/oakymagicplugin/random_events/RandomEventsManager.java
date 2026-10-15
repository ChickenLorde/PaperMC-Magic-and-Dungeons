package me.oakyplugin.oakymagicplugin.random_events;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.random_events.event_entities.FallingStarEntity;
import me.oakyplugin.oakymagicplugin.random_events.events.FallingStarEvent;
import me.oakyplugin.oakymagicplugin.utility.BiMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;

public class RandomEventsManager {

    private final HashSet<RandomEvent> randomEvents = new HashSet<>();

    private final HashSet<RandomEventEntity> eventEntitiesToAdd = new HashSet<>();
    private final HashSet<RandomEventEntity> eventEntities = new HashSet<>();

    private final BiMap<FallingStarEntity,Interaction> starInteractionPairs = new BiMap<>();

    private Long tick = 0L;

    public void registerRandomEvent(RandomEvent event) {
        randomEvents.add(event);
    }

    public void registerEventEntity(RandomEventEntity entity) {
        eventEntitiesToAdd.add(entity);
    }

    public void tick() {
        tick++;
        for(RandomEvent event : randomEvents) {
            if(tick % event.getOccurInterval() == 0)
                event.tryToOccur();
        }

        eventEntities.addAll(eventEntitiesToAdd);
        eventEntitiesToAdd.clear();
        for(RandomEventEntity entity : eventEntities) {
            entity.tick();
        }
        eventEntities.removeIf(x -> !x.getIsAlive());
    }

    public void registerRandomEvents() {
        registerRandomEvent(new FallingStarEvent());
    }

    @SuppressWarnings("UnstableApiUsage")
    public void linkInteractionToStar(FallingStarEntity star) {
        Interaction interaction = star.getLocation().getWorld().spawn(star.getLocation(), Interaction.class);

        interaction.setInteractionHeight(0.35f);
        interaction.setInteractionWidth(0.35f);

        interaction.getPersistentDataContainer().set(OakyMagicPlugin.REMOVE_ON_DISABLE, PersistentDataType.BOOLEAN, true);
        star.linkInteraction(interaction);

        boolean linked = starInteractionPairs.add(star, interaction);
        if(!linked)
            Bukkit.getLogger().warning("Could not link star and interaction!");
    }

    public BiMap<FallingStarEntity, Interaction> getStarInteractionPairs() { return starInteractionPairs;}
}
