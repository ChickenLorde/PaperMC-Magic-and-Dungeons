package me.oakyplugin.oakymagicplugin.dungeons;

import me.oakyplugin.oakymagicplugin.dungeons.rooms.DungeonRoomTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class DungeonGenerator {

    private int maxRooms;
    private int maxBranches;

    private DungeonPreset preset;


    public void testGenerate() {
        preset = DungeonType.RUINS_TIER_1.getPreset();
    }

}
