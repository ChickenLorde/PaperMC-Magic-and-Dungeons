package me.oakyplugin.oakymagicplugin.dungeons.rooms;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RoomTemplatePool {
    private List<PoolEntry> entries;

    public RoomTemplatePool() {
        entries = new ArrayList<>();
    }
    private RoomTemplatePool(List<PoolEntry> entries) {
        this.entries = entries;
    }

    public void add(DungeonRoomTemplate template, int weight) {
        if(weight <= 0) return;

        for(PoolEntry entry : entries) {
            if(entry.template.equals(template)) return;
        }
        entries.add(new PoolEntry(template, weight));
    }

    public void remove(DungeonRoomTemplate template) {
        for(PoolEntry entry : new ArrayList<>(entries)) {
            if(entry.template.equals(template)) {
                entries.remove(entry);
                return;
            }
        }
    }

    public int entryCount() {return entries.size();}

    public DungeonRoomTemplate roll() {
        int totalWeight = 0;
        Random rand = new Random();

        for(PoolEntry entry : entries) {
            totalWeight += entry.weight;
        }

        if(totalWeight <= 0) return null;

        int roll = rand.nextInt(totalWeight);

        for(PoolEntry entry : entries) {
            roll -= entry.weight;

            if(roll < 0) {
                return entry.template;
            }
        }
        if(!entries.isEmpty())
            return entries.getLast().template;
        else
            return null;
    }

    public static PoolEntry entry(String templateId, int weight) {
        DungeonRoomTemplate template = OakyMagicPlugin.getPlugin().getDungeonRoomTemplateRegistry().getTemplate(templateId);
        if(template == null) return null;
        return new PoolEntry(template, weight);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<PoolEntry> entries;

        private Builder() {}

        public Builder add(String templateId, int weight) {
            PoolEntry entry = entry(templateId, weight);
            if(entry != null)
                entries.add(entry(templateId, weight));
            return this;
        }

        public RoomTemplatePool build() {
            return new RoomTemplatePool(entries);
        }
    }


    public static class PoolEntry {

        private final DungeonRoomTemplate template;
        private final int weight;
        private PoolEntry(DungeonRoomTemplate template, int weight) {
            this.template = template;
            this.weight = weight;
        }
    }
}