package me.oakyplugin.oakymagicplugin.entity;

public class MagicStats {

    private double baseDamageMultiplier;
    private double baseMagicArmor;

    private double extraDamageMultiplier;
    private double extraMagicArmor;

    private MagicStats(MagicStatsBuilder builder) {
        baseDamageMultiplier = builder.baseDamageMultiplier;
        baseMagicArmor = builder.baseMagicArmorPoints;

        extraMagicArmor = 0;
        extraDamageMultiplier = 0;
    }
    public MagicStats() {
        baseDamageMultiplier = 0;
        baseMagicArmor = 0;

        extraMagicArmor = 0;
        extraDamageMultiplier = 0;
    }

    public static MagicStatsBuilder builder() {
        return new MagicStatsBuilder();
    }

    public double getBaseDamageMultiplier() {return baseDamageMultiplier;}
    public double getBaseMagicArmor() {return baseMagicArmor;}
    public double getExtraDamageMultiplier() {return extraDamageMultiplier;}
    public double getExtraMagicArmor() {return extraMagicArmor;}

    public void setExtraDamageMultiplier(double extraDamageMultiplier) {this.extraDamageMultiplier = extraDamageMultiplier;}
    public void setExtraMagicArmor(double extraMagicArmor) {this.extraMagicArmor = extraMagicArmor;}

    public double getTotalDamageMultiplier() { return baseDamageMultiplier + extraDamageMultiplier; }
    public double getTotalMagicArmor() { return baseMagicArmor + extraMagicArmor; }

    public String toString() {
        return "baseDmgMult: "+baseDamageMultiplier+"\nextraDmgMult: "+extraDamageMultiplier+"\nbaseArmor: "+ baseMagicArmor +"\nextraArmor: "+ extraMagicArmor;
    }

    public static final class MagicStatsBuilder {
        private double baseDamageMultiplier;
        private double baseMagicArmorPoints;

        private MagicStatsBuilder() {
            baseMagicArmorPoints = 0;
            baseDamageMultiplier = 0;
        }

        public MagicStatsBuilder damageMultiplier(double amount) { baseDamageMultiplier = amount; return this;}
        public MagicStatsBuilder magicArmor(double amount) { baseMagicArmorPoints = amount; return this;}
        public MagicStats build() {
            return new MagicStats(this);
        }


    }

}
