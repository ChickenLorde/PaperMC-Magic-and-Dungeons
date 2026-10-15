package me.oakyplugin.oakymagicplugin.dungeons;

import me.oakyplugin.oakymagicplugin.dungeons.rooms.RoomTemplatePool;

import java.util.HashMap;

public enum DungeonType {
    RUINS_TIER_1(new DungeonPreset(
            DungeonTier.TIER_1,
            RoomTemplatePool.builder()
            .add("entrance:ruins_0",1)
            .build(),
            RoomTemplatePool.builder()
                    .add("corridor:ruins_straight_0",11)
                    .add("corridor:ruins_split_0",3)
                    .add("corridor:ruins_stairs_0",2)
                    .add("corridor:ruins_stairs_1",2)
                    .add("corridor:ruins_stairs_2",2)
                    .add("corridor:ruins_corner_0",2)
                    .add("corridor:ruins_corner_1",2)
            .build(),
            RoomTemplatePool.builder()
                    .add("parkour:ruins_0",1)
                    .build()
    ));

    private final DungeonPreset preset;


    DungeonType(DungeonPreset preset) {
        this.preset = preset;
    }

    public DungeonPreset getPreset() {
        return preset;
    }
}
