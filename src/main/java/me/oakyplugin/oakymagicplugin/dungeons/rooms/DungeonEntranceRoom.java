package me.oakyplugin.oakymagicplugin.dungeons.rooms;

import me.oakyplugin.oakymagicplugin.dungeons.Dungeon;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.io.IOException;

public class DungeonEntranceRoom extends DungeonRoom {

    private final Location playerSpawnLocation;
    private RoomExit exit;

    public DungeonEntranceRoom(Dungeon master, DungeonRoomTemplate template, Location placeLocation) throws IOException {
        super(master, template, CardinalDirection.SOUTH, placeLocation, 0);

        Location localSpawn = null;
        for(Entity e : getTemplate().getStructure().getEntities()) {
            if(!(e instanceof ArmorStand a)) continue;
            if(a.getScoreboardTags().contains("dungeon_player_spawn"))
                localSpawn = a.getLocation();
        }
        if(localSpawn == null) {
            throw new RuntimeException("Dungeon Entrance does not contain an ArmorStand marking the player spawn");
        }

        playerSpawnLocation = placeLocation.clone().add(localSpawn.toVector().add(new Vector(0,0,0.5)));
        playerSpawnLocation.setDirection(localSpawn.getDirection());

        for(RoomExit ex : getExits()) {
            exit = ex;
        }
    }

    public Location getPlayerSpawnLocation() {return playerSpawnLocation;}
    public RoomExit getExit() {return exit;}
}
