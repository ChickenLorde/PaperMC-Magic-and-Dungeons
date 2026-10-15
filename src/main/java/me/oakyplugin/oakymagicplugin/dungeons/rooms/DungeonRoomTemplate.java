package me.oakyplugin.oakymagicplugin.dungeons.rooms;

import me.oakyplugin.oakymagicplugin.utility.Utility;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.structure.Structure;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;

public class DungeonRoomTemplate {
    private final DungeonRoomType type;
    private final Structure structure;
    private final Vector size;

    private final HashSet<DungeonExitTemplate> exits;

    private Vector entranceOffset;

    public DungeonRoomTemplate(DungeonRoomType type, Structure structure) {
        this.type = type;
        this.structure = structure;

        size = structure.getSize();

        exits = new HashSet<>();
        entranceOffset = new Vector(0,0,0);

        for(Entity e : structure.getEntities()) {
            if(!(e instanceof ArmorStand a)) continue;

            if(a.getScoreboardTags().contains("dungeon_room_exit")) {
                exits.add(new DungeonExitTemplate(Utility.centerVectorXZ(a.getLocation().toVector()), Utility.entityFacingToCardinal(a)));
            } else if(a.getScoreboardTags().contains("dungeon_room_entrance"))
                entranceOffset = Utility.centerVectorXZ(a.getLocation().toVector());
        }
    }

    public DungeonRoomType getType() {
        return type;
    }
    public Structure getStructure() {
        return structure;
    }
    public Vector getSize() {
        return size;
    }
    public HashSet<DungeonExitTemplate> getExits() {
        return exits;
    }
    public Vector getEntranceOffset() {
        return entranceOffset;
    }

    public static class DungeonExitTemplate {
        private final Vector localOffset;
        private final CardinalDirection direction;

        private DungeonExitTemplate(Vector offset, CardinalDirection dir) {
            localOffset = offset;
            direction = dir;
        }

        public Vector getOffset() {return localOffset;}
        public CardinalDirection getDirection() {return direction;}
    }
}
