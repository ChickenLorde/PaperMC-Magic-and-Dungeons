package me.oakyplugin.oakymagicplugin.dungeons;

import me.oakyplugin.oakymagicplugin.dungeons.rooms.RoomTemplatePool;

public class DungeonPreset {

    private final DungeonTier tier;
    private final RoomTemplatePool entrancePool;
    private final RoomTemplatePool corridorPool;
    private final RoomTemplatePool parkourPool;

    public DungeonPreset(DungeonTier tier, RoomTemplatePool entrancePool, RoomTemplatePool corridorPool, RoomTemplatePool parkourPool) {
        this.tier = tier;
        this.entrancePool = entrancePool;
        this.corridorPool = corridorPool;
        this.parkourPool = parkourPool;
    }

    public DungeonTier getTier() {return tier;}
    public RoomTemplatePool getEntrancePool() {return entrancePool;}
    public RoomTemplatePool getCorridorPool() {return corridorPool;}
    public RoomTemplatePool getParkourPool() {return parkourPool;}
}
