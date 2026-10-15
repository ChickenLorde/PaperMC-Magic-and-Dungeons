package me.oakyplugin.oakymagicplugin.items.spells.magic_circles;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class MagicCircleManager {

    private final Set<MagicCircle> magicCircles = new HashSet<>();

    public void tick() {
        for(MagicCircle m : magicCircles)
            m.tick();
        magicCircles.removeIf(MagicCircle::shouldRemove);
    }
    protected void addMagicCircle(MagicCircle circle) {
        magicCircles.add(circle);
    }

    public HashSet<MagicCircle> getPlayerMagicCircles(Player p) {
        HashSet<MagicCircle> circles = new HashSet<>();

        for(MagicCircle circle : magicCircles) {
            if(circle.getOwner().equals(p)) circles.add(circle);
        }
        return circles;
    }
}
