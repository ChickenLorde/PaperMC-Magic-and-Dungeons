package me.oakyplugin.oakymagicplugin.items.spells.magic_circles;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;

public abstract class MagicCircle {

    private final MagicCircleManager manager;
    private final Player owner;
    private final Location location;
    private final Spell spell;

    protected ItemDisplay itemDisplay;

    private boolean isDespawning;
    private boolean shouldRemove;
    private int delay;

    public MagicCircle(Player owner, Location location, Spell spell, int delay) {
        this.owner = owner;
        this.spell = spell;
        this.location = location;

        isDespawning = false;
        shouldRemove = false;
        this.delay = delay;



        manager = OakyMagicPlugin.getPlugin().getMagicCircleManager();

        manager.addMagicCircle(this);
    }

    protected void tick() {
        if(delay > 0) {
            delay--;
            return;
        }
        if(shouldRemove) return;
        if(isDespawning) whileDespawn();
        tickExtra();
    }

    public void remove() {
        itemDisplay.remove();
        itemDisplay = null;
        shouldRemove = true;
    }
    public void startDespawn() { isDespawning = true; }
    protected abstract void tickExtra();
    protected abstract void whileDespawn();

    public boolean shouldRemove() {return shouldRemove;}
    public ItemDisplay getDisplay() {return itemDisplay; }

    public Player getOwner(){return owner;}

    public boolean isDespawning() {return isDespawning;}
}
