package me.oakyplugin.oakymagicplugin.player.data;

import me.oakyplugin.oakymagicplugin.entity.MagicStats;

public class StatBonus {

    private final double maxManaBonus;
    private final double maxManaRegenBonus;
    private final double manaRegenRateBonus;
    private final MagicStats stats;


    private StatBonus(StatBonusBuilder builder) {
        this.maxManaBonus = builder.maxManaBonus;
        this.maxManaRegenBonus = builder.maxManaRegenBonus;
        this.manaRegenRateBonus = builder.manaRegenRateBonus;
        this.stats = builder.stats;
    }

    public static StatBonusBuilder builder() {
        return new StatBonusBuilder();
    }

    public double getMaxManaBonus() {return maxManaBonus;}
    public double getMaxManaRegenBonus() {return maxManaRegenBonus;}
    public double getManaRegenRateBonus() {return manaRegenRateBonus;}
    public MagicStats getMagicStats() {return stats;}

    public static class StatBonusBuilder {
        private double maxManaBonus;
        private double maxManaRegenBonus;
        private double manaRegenRateBonus;
        private MagicStats stats;


        private StatBonusBuilder() {
            maxManaBonus = maxManaRegenBonus = manaRegenRateBonus = 0;
            stats = new MagicStats();
        }

        public StatBonusBuilder maxManaBonus(double amount) {maxManaBonus = amount; return this;}
        public StatBonusBuilder maxManaRegenBonus(double amount) {maxManaRegenBonus = amount; return this;}
        public StatBonusBuilder manaRegenRateBonus(double amount) {manaRegenRateBonus = amount; return this;}
        public StatBonusBuilder magicStats(MagicStats stats) { this.stats = stats; return this;}

        public StatBonus build() {
            return new StatBonus(this);
        }

    }
}
