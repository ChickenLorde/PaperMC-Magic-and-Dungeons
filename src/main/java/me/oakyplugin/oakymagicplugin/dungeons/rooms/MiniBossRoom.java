package me.oakyplugin.oakymagicplugin.dungeons.rooms;

import me.oakyplugin.oakymagicplugin.dungeons.Dungeon;
import org.bukkit.Location;

import java.io.IOException;

public class MiniBossRoom extends DungeonRoom {



    public MiniBossRoom(Dungeon master, DungeonRoomTemplate template, CardinalDirection orientation, Location placeLocation, int iteration) throws IOException {
        super(master, template, orientation, placeLocation, iteration);
    }

}
