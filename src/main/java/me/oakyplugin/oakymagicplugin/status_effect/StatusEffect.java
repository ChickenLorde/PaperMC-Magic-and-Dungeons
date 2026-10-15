package me.oakyplugin.oakymagicplugin.status_effect;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;

import java.util.Locale;
import java.util.UUID;

public abstract class StatusEffect {
    private boolean isActive;

    private OakyMagicPlugin plugin = OakyMagicPlugin.getPlugin();

    private String id;
    private int maxDurationTicks;
    private int ticksActive;
    //Interval, in ticks, for the effect to trigger (i.e. damage)
    private int triggerInterval;

    private UUID target;

    public StatusEffect(String id, int maxDurationTicks, int triggerInterval, UUID target) {
        this.id = id.toLowerCase(Locale.ROOT).replace(" ","_");
        this.maxDurationTicks = maxDurationTicks;
        this.ticksActive = 0;
        this.triggerInterval = triggerInterval;
        this.target = target;
        isActive = true;
        onApply();

    }

    final void tick() {
        if(!isActive) return;
        if(ticksActive > maxDurationTicks) {
            end();
            return;
        }
        if(ticksActive % triggerInterval == 0 && ticksActive != 0) onTrigger();

        onTick();

        ticksActive++;
    }

    public abstract void onTick();
    public abstract void onApply();
    public abstract void onExpire();
    public abstract void onTrigger();
    public void end() {
        ticksActive = maxDurationTicks;
        onExpire();
        isActive = false;
    }

    public boolean getIsActive() {return isActive;}
    public UUID getTarget() {return target;}

    /*
        Effects must be the same type. If the maxDuration of this effect is greater than the other, then just subtract the current ticksActive from the other's max ticks.
     */
    public void merge(StatusEffect other) {
        if(other.getClass() != this.getClass()) return;
        if(maxDurationTicks >= other.maxDurationTicks) {
            ticksActive = Math.max(0, ticksActive - other.maxDurationTicks);
            return;
        }

        ticksActive = 0;
        maxDurationTicks = other.maxDurationTicks;
    }

    public OakyMagicPlugin getPlugin() {return plugin;}

    public int getTicksActive() {
        return ticksActive;
    }

    public int getTriggerInterval() {
        return triggerInterval;
    }

    public String getId() {
        return id;
    }

    public int getMaxDurationTicks() {
        return maxDurationTicks;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }
}
