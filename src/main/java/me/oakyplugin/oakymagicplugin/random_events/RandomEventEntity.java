package me.oakyplugin.oakymagicplugin.random_events;

public abstract class RandomEventEntity {

    private final RandomEventsManager manager;
    protected boolean isAlive;
    private int ticksAlive;

    public RandomEventEntity(RandomEventsManager manager) {
        isAlive = true;
        ticksAlive = 0;
        this.manager = manager;
        this.manager.registerEventEntity(this);
    }

    public final void tick() {
        if(!isAlive) return;
        ticksAlive++;
        onTick();
    }
    public abstract void onTick();
    public abstract void onRemove();


    public boolean getIsAlive() { return isAlive; }

    public int getTicksAlive() {return ticksAlive;}

    public RandomEventsManager getManager() {return manager;}
}
