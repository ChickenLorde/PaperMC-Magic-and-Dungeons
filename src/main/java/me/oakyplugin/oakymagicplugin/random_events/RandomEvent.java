package me.oakyplugin.oakymagicplugin.random_events;

public abstract class RandomEvent {

    private int occurInterval;
    private double occurChance;
    private double occurChanceMult;

    public RandomEvent(int occurInterval, double occurChance) {
        this.occurInterval = Math.max(1,occurInterval);
        this.occurChance = Math.clamp(occurChance, 0, 1);
        this.occurChanceMult = 1;
    }

    public final void tryToOccur() {
        if(!precondition()) return;
        onTryToOccur();

    }
    protected void onTryToOccur() {}

    protected boolean rollForOccurrence() {
        double random = (Math.random());

        return random <= occurChance * occurChanceMult;
    }

    public boolean precondition() { return true; }

    public int getOccurInterval() {return occurInterval;}
    public double getOccurChance() {return occurChance;}
    protected void setOccurChanceMult(double mult) {occurChanceMult = mult; }
}
