package me.oakyplugin.oakymagicplugin.items.spells.shield;

import org.bukkit.entity.Player;

import java.util.HashSet;

public class ShieldManager {

    private final HashSet<Shield> shieldSet;

    public ShieldManager() {
        shieldSet = new HashSet<>();
    }

    public void tick() {
        for(Shield shield : shieldSet) {
            shield.tick();
        }
        shieldSet.removeIf(s -> !s.isActive());
    }

    protected void addShield(Shield s) {
        shieldSet.add(s);
    }

    public boolean playerHasShield(Player p) {
        for(Shield s : shieldSet)
            if(s.getOwner().equals(p)) return true;
        return false;
    }

    public HashSet<Shield> getShields() {return shieldSet;}
}
