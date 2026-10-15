package me.oakyplugin.oakymagicplugin.dungeons.rooms;

import me.oakyplugin.oakymagicplugin.dungeons.Dungeon;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import org.bukkit.*;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.structure.Structure;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.smartcardio.Card;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class DungeonRoom {

    private final Dungeon master;

    private Vector entranceOffset;
    private final Location placeLocation;
    private Vector size;
    private final CardinalDirection orientation;
    private final DungeonRoomTemplate template;
    private final int iteration;

    private final HashSet<RoomExit> exits;

    //Should rotate the entrance and exit from the template and also change the place location so it's aligned with the entrance aligns with the place location
    public DungeonRoom(
            Dungeon master,
            DungeonRoomTemplate template,
            CardinalDirection orientation,
            Location placeLocation,
            int iteration
    ) throws IOException {
        this.template = template;

        this.master = master;
        this.iteration = iteration;
        exits = new HashSet<>();
        this.orientation = orientation;

        entranceOffset = template.getEntranceOffset();

        //Rotate the entrance offset to match the structure's orientation
        switch(orientation) {
            case SOUTH: break;
            case WEST: entranceOffset = new Vector(-entranceOffset.getZ(), entranceOffset.getY(), entranceOffset.getX()); break;
            case NORTH: entranceOffset = new Vector(-entranceOffset.getX(), entranceOffset.getY(), -entranceOffset.getZ()); break;
            case EAST: entranceOffset = new Vector(entranceOffset.getZ(), entranceOffset.getY(), -entranceOffset.getX()); break;
        }

        //Convert each exit's local offset and direction to absolute
        for(DungeonRoomTemplate.DungeonExitTemplate exit : template.getExits()) {
            Vector offset = exit.getOffset();

            //Set its absolute offset based on the structure orientation
            offset = switch(orientation) {
                case SOUTH -> offset;
                case EAST -> Utility.flatRotateVector(offset, Utility.FlatRotation.CW_90);
                case NORTH -> Utility.flatRotateVector(offset, Utility.FlatRotation.FLIP_180);
                case WEST -> Utility.flatRotateVector(offset, Utility.FlatRotation.CCW_90);
            };

            //Same but for direction
            CardinalDirection dir = exit.getDirection();

            for(int i = 0; i < switch(orientation) {
                case SOUTH -> 0;
                case EAST -> 1;
                case NORTH -> 2;
                case WEST -> 3;}; i++) {
                dir = switch(dir) {
                    case SOUTH -> CardinalDirection.EAST;
                    case EAST -> CardinalDirection.NORTH;
                    case NORTH -> CardinalDirection.WEST;
                    case WEST -> CardinalDirection.SOUTH;
                };
            }

            exits.add(new RoomExit(this, offset, dir));

        }

        this.placeLocation = Utility.centerVectorXZ(placeLocation.toVector()).subtract(entranceOffset).toLocation(placeLocation.getWorld());
        switch(orientation) {
            case NORTH -> this.placeLocation.subtract(0,0,1);
            case EAST -> this.placeLocation.add(1,0,0);
            case WEST -> this.placeLocation.subtract(1,0,0);
            default -> this.placeLocation.add(0,0,1);
        }

        size = template.getSize().clone();

        switch(orientation) {
            case EAST, WEST -> size = new Vector(size.getZ(), size.getY(), size.getX());
        }
    }

    public BoundingBox getRoomBounds() {
        Vector lowerCorner,upperCorner;

        switch(orientation) {
            case NORTH: lowerCorner = placeLocation.toVector().subtract(new Vector(size.getX() - 1, 0, size.getZ() - 1)); upperCorner = placeLocation.toVector().add(new Vector(1,size.getY(),1)); break;
            case WEST: lowerCorner = placeLocation.toVector().subtract(new Vector(size.getX() - 1, 0,0)); upperCorner = placeLocation.toVector().add(new Vector(1,size.getY(),size.getZ())); break;
            case EAST: lowerCorner = placeLocation.toVector().subtract(new Vector(0,0,size.getZ() - 1)); upperCorner = placeLocation.toVector().add(new Vector(size.getX(), size.getY(), 1)); break;
            default: lowerCorner = placeLocation.toVector(); upperCorner = placeLocation.toVector().add(size); break;
        }

        lowerCorner = new Vector(Math.floor(lowerCorner.getX()),Math.floor(lowerCorner.getY()),Math.floor(lowerCorner.getZ()));
        upperCorner = new Vector(Math.floor(upperCorner.getX()),Math.floor(upperCorner.getY()),Math.floor(upperCorner.getZ()));

        Particle.DUST.builder()
                .location(lowerCorner.toLocation(placeLocation.getWorld()))
                .color(Color.RED, 4.0f)
                .count(3)
                .offset(0.1,0.1,0.1)
                .spawn();
        Particle.DUST.builder()
                .location(upperCorner.toLocation(placeLocation.getWorld()))
                .color(Color.BLUE, 4.0f)
                .count(3)
                .offset(0.1,0.1,0.1)
                .spawn();
        Particle.DUST.builder()
                .location(placeLocation)
                .color(Color.YELLOW, 4.0f)
                .count(3)
                .offset(0.1,0.1,0.1)
                .spawn();

        for(RoomExit exit : exits) {
            Particle.DUST.builder()
                    .location(exit.getLocation())
                    .color(Color.GREEN, 4.0f)
                    .count(3)
                    .offset(0.1,0.1,0.1)
                    .spawn();
        }

        return BoundingBox.of(lowerCorner, upperCorner);

    }

    public boolean isPlacementValid() {
        if(master == null) return true;

        ArrayList<DungeonRoom> rooms = master.getRooms();
        for(DungeonRoom room : rooms) {
            if(room.equals(this)) continue;

            if(room.getRoomBounds().contains(getRoomBounds()))
                return false;
        }
        return true;
    }

    public void deleteRoomFromWorld() {
        BoundingBox bounds = getRoomBounds();
        World w = placeLocation.getWorld();
        for(int i = (int)bounds.getMinX(); i < bounds.getMaxX(); i++) {
            for(int j = (int)bounds.getMinY(); j < bounds.getMaxY(); j++) {
                for(int k = (int)bounds.getMinZ(); k < bounds.getMaxZ(); k++) {
                    w.getBlockAt(i,j,k).setType(Material.AIR);
                }
            }
        }
    }

    public void placeRoom() {
        StructureRotation rotation = switch(orientation) {
            case NORTH -> StructureRotation.CLOCKWISE_180;
            case EAST -> StructureRotation.COUNTERCLOCKWISE_90;
            case WEST -> StructureRotation.CLOCKWISE_90;
            case SOUTH -> StructureRotation.NONE;
        };
        template.getStructure().place(placeLocation,false, rotation, Mirror.NONE, 0, 1, new Random());
    }

    public CardinalDirection getOrientation() {return orientation;}
    public HashSet<RoomExit> getExits() {return exits;}
    public Location getPlaceLocation() {return placeLocation;}
    public DungeonRoomTemplate getTemplate() {return template; }
    public int getIteration() {return iteration;}

    public static class RoomExit {
        private DungeonRoom room;
        private Vector offset;
        private CardinalDirection direction;

        private DungeonRoom connectedRoom;

        protected RoomExit(DungeonRoom room,Vector offset, CardinalDirection direction) {
            this.room = room;
            this.offset = offset;
            this.direction = direction;
        }

        public boolean hasConnectedRoom() {return connectedRoom != null;}
        public Vector getOffset() {return offset;}
        public Location getLocation() {
            Location l = room.getPlaceLocation().clone().add(offset);
            l.set(Math.floor(l.getX()),Math.floor(l.getY()),Math.floor(l.getZ()));
            Particle.DUST.builder()
                    .location(l)
                    .color(Color.RED, 4.0f)
                    .count(5)
                    .spawn();
            return l;
        }
        public CardinalDirection getDirection() {return direction;}
    }
}
