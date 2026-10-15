package me.oakyplugin.oakymagicplugin.dungeons;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.dungeons.rooms.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Dungeon {

    private int id;
    private Location origin;
    private final HashSet<Player> players = new HashSet<>();
    private final HashMap<Player, Location> playerEnterLocations = new HashMap<>();
    private final ArrayList<DungeonRoom> rooms = new ArrayList<>();
    private DungeonEntranceRoom entranceRoom;
    private Location entranceLocation;

    private final DungeonManager dungeonManager = OakyMagicPlugin.getPlugin().getDungeonManager();

    private boolean isActive = true;

    private static final NamespacedKey dungeonWorldKey = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "dungeon_world_key");

    public Dungeon() {
        dungeonManager.registerDungeon(this);
    }

    void tick() {

    }

    public void testCreateDungeon() throws IOException {
        final double dungeonsPerRow = 100;
        final double spacing = 512;
        id = (int)(Math.random() * 101);
        Vector position = new Vector(id * spacing % (spacing * dungeonsPerRow),64, id / dungeonsPerRow * spacing);
        World dungeonWorld = Bukkit.getWorld("dungeon_world");
        if(dungeonWorld == null) return;
        entranceLocation = position.toLocation(dungeonWorld);

        DungeonRoomTemplateRegistry reg = OakyMagicPlugin.getPlugin().getDungeonRoomTemplateRegistry();

        DungeonEntranceRoom entrance = new DungeonEntranceRoom(this, reg.getTemplate("entrance:ruins_0"),entranceLocation);
        rooms.add(entrance);
        entranceRoom = entrance;


        for(int i = 0; i < 8; i++) {

            for(DungeonRoom next : getIterationRooms(i)) {
                for (DungeonRoom.RoomExit exit : next.getExits()) {
                    if (i == 1 || i == 3) {
                        int rand = (int)(Math.random() * 3);
                        if (rand == 0)
                            rooms.add(new DungeonRoom(this, reg.getTemplate("corridor:ruins_split_0"), exit.getDirection(), exit.getLocation(), i + 1));
                        else if(rand == 1) {
                            RoomTemplatePool pool = new RoomTemplatePool();
                            pool.add(reg.getTemplate("corridor:ruins_stairs_0"),1);
                            pool.add(reg.getTemplate("corridor:ruins_stairs_1"),1);
                            pool.add(reg.getTemplate("corridor:ruins_stairs_2"),1);
                            rooms.add(new DungeonRoom(this, pool.roll(), exit.getDirection(), exit.getLocation(), i + 1));
                        } else
                            rooms.add(new DungeonRoom(this, reg.getTemplate("parkour:ruins_0"), exit.getDirection(), exit.getLocation(), i + 1));

                    } else {
                        RoomTemplatePool pool = new RoomTemplatePool();
                        pool.add(reg.getTemplate("corridor:ruins_corner_0"),1);
                        pool.add(reg.getTemplate("corridor:ruins_corner_1"),1);
                        pool.add(reg.getTemplate("corridor:ruins_straight_0"),1);

                        DungeonRoom room = new DungeonRoom(this, pool.roll(), exit.getDirection(), exit.getLocation(), i + 1);

                        while (!room.isPlacementValid() && pool.entryCount() != 0) {
                            pool.remove(room.getTemplate());
                            room = new DungeonRoom(this, pool.roll(), exit.getDirection(), exit.getLocation(), i + 1);
                        }
                    }
                }
            }
        }
        placeRooms();
    }

    public void addRoom(DungeonRoom room) {
        if(rooms.contains(room)) return;
        rooms.add(room);
    }

    public void placeRooms() {
        new BukkitRunnable() {
            int index = 0;
            @Override
            public void run() {
                if(index > rooms.size() - 1) {
                    cancel();
                    return;
                }
                rooms.get(index).placeRoom();
                index++;
            }
        }.runTaskTimer(OakyMagicPlugin.getPlugin(), 0L, 3L);
    }

    public void deleteDungeon() {

        for(Player p : players) {
            removePlayer(p);
        }

        new BukkitRunnable() {
            int roomIndex = 0;

            @Override
            public void run() {
                if(roomIndex > rooms.size() - 1) {
                    this.cancel();
                    return;
                }
                rooms.get(roomIndex).deleteRoomFromWorld();
                roomIndex++;
            }
        }.runTaskTimer(OakyMagicPlugin.getPlugin(), 10L, 4L);
    }

    public void addPlayer(Player p) {
        players.add(p);

        AttributeInstance a = p.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if(a != null && a.getModifier(dungeonWorldKey) == null) {
            a.addModifier(new AttributeModifier(dungeonWorldKey, -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        }
    }

    public void teleportPlayer(Player p) {
        if(!players.contains(p)) return;
        playerEnterLocations.put(p, p.getLocation());
        p.teleport(entranceRoom.getPlayerSpawnLocation());
    }

    public void removePlayer(Player p) {
        if(!players.contains(p)) return;
        players.remove(p);
        p.teleport(playerEnterLocations.get(p));
        playerEnterLocations.remove(p);

        AttributeInstance a = p.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if(a != null && a.getModifier(dungeonWorldKey) != null) {
            a.removeModifier(dungeonWorldKey);
        }
    }

    public ArrayList<DungeonRoom> getIterationRooms(int iteration) {
        ArrayList<DungeonRoom> rooms = new ArrayList<>();
        for(DungeonRoom room : this.rooms) {
            if(room.getIteration() == iteration)
                rooms.add(room);
        }
        return rooms;
    }

    public ArrayList<DungeonRoom> getRooms() {
        return rooms;
    }

    public Location getOrigin() {return origin;}
    public boolean getIsActive() {return isActive;}
    public HashSet<Player> getPlayers() {return players;}

}
