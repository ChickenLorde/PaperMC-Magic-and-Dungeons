package me.oakyplugin.oakymagicplugin.dungeons;

import org.bukkit.entity.Player;

import java.util.HashSet;

public class DungeonManager {

    private final HashSet<Dungeon> dungeons = new HashSet<>();

    void registerDungeon(Dungeon d) {
        dungeons.add(d);
    }

    public void tick() {
        for(Dungeon d : dungeons) {
            d.tick();
            if(!d.getIsActive())
                d.deleteDungeon();
        }
        dungeons.removeIf(x -> !x.getIsActive());
    }

    public void addPlayerToDungeon(Player p, Dungeon d) {
        if(p == null || d == null) return;
        if(isPlayerInDungeon(p)) return;
        d.addPlayer(p);
    }
    public void removePlayerFromDungeons(Player p) {
        if(p == null) return;

        for(Dungeon d : dungeons) {
            d.removePlayer(p);
        }
    }

    public void closeDungeon(Dungeon d) {
        d.deleteDungeon();
    }
    public void closeAllDungeons() {
        for(Dungeon d : dungeons)
            d.deleteDungeon();
    }

    public void removeAllPlayers() {
        for(Dungeon d : dungeons) {
            for(Player p : d.getPlayers())
                d.removePlayer(p);
        }
    }

    public boolean isPlayerInDungeon(Player p) {
        for(Dungeon d : dungeons) {
            if(d.getPlayers().contains(p))
                return true;
        }
        return false;
    }
}
