package me.oakyplugin.oakymagicplugin.player.data;

import me.oakyplugin.oakymagicplugin.entity.MagicStats;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PlayerStats {

    private double mana, maxMana;
    //Mana regen of 0.05 = 1 mana/s
    private double maxManaRegen, manaRegenRate, manaRegen;
    private double extraMaxMana, extraDamageMultiplier, extraMaxManaRegen, extraManaRegenRate;
    //Integer should be the start timestamp of the cooldown, not the duration
    private final Map<String, Integer> spellCooldowns;
    private double globalCooldown;
    private double exhaustion;
    private double exhaustionDecayRate;
    private final double MAX_EXHAUSTION_DECAY_RATE = 1.25;

    private final MagicStats magicStats;

    PlayerStats(double mana, double maxMana,double manaRegenRate, double maxManaRegen, MagicStats magicStats) {
        this.mana = mana;
        this.maxMana = maxMana;
        this.manaRegenRate = manaRegenRate;
        this.maxManaRegen = maxManaRegen;
        this.extraMaxMana = this.extraDamageMultiplier = this.extraMaxManaRegen = this.extraManaRegenRate = 0;
        this.magicStats = magicStats;
        spellCooldowns = new HashMap<>();
    }
    public PlayerStats() {
        this(0, 100,0.0003, 0.5, new MagicStats());
    }

    public double getMana() {return mana;}
    public double getBaseMaxMana() {return maxMana;}
    public double getBaseMaxManaRegen() {return maxManaRegen;}
    public double getBaseManaRegenRate() {return manaRegenRate;}
    public double getManaRegen() {return manaRegen;}
    public double getGlobalCooldown() {return globalCooldown;}
    public MagicStats getMagicStats() {return magicStats;}

    public void setExtraMaxMana(double extraMaxMana) {this.extraMaxMana = extraMaxMana;}
    public void setExtraMaxManaRegen(double extraMaxManaRegen) {this.extraMaxManaRegen = extraMaxManaRegen;}
    public void setExtraManaRegenRate(double extraManaRegenRate) {this.extraManaRegenRate = extraManaRegenRate;}

    public double getExtraMaxMana() {return extraMaxMana;}
    public double getExtraDamageMultiplier() {return extraDamageMultiplier;}
    public double getExtraMaxManaRegen() {return extraMaxManaRegen;}
    public double getExtraManaRegenRate() {return extraManaRegenRate;}

    public double getTotalMaxMana() {return maxMana + extraMaxMana;}
    public double getTotalMaxManaRegen() {return maxManaRegen + extraMaxManaRegen;}
    public double getTotalManaRegenRate() {return manaRegenRate + extraManaRegenRate;}
    public double getExhaustion() { return exhaustion;}

    public void setMana(double amount) {
        mana = Math.clamp(amount,0,getTotalMaxMana());
    }
    public void addMana(double amount) {
        if(amount <= 0)
            throw new IllegalArgumentException("Mana amount must be a positive integer");
        mana = Math.clamp(mana + amount, 0, getTotalMaxMana());
    }
    public void removeMana(double amount) {
        if(amount <= 0)
            throw new IllegalArgumentException("Mana amount must be a positive integer");
        mana = Math.clamp(mana - amount, 0, getTotalMaxMana());
    }
    public void setMaxMana(double amount)
    {
        maxMana = Math.max(amount, 0);
    }

    public void addExhaustion(double amount) {
        if(amount < 0)
            throw new IllegalArgumentException("Exhaustion amount cannot be negative");
        if(amount == 0) return;

        manaRegen -= 5 * (manaRegen - (manaRegen / (0.065 * amount + 1)));
        if(manaRegen < 0) manaRegen = 0;

        //Exhaustion decay rate should reset if exhaustion is low, but should be impacted less and less the more exhaustion you already have
        exhaustionDecayRate *= 1 - Math.pow(Math.E, (-0.01 * exhaustion));
        Bukkit.getLogger().info("Exhaustion Decay Rate: "+exhaustionDecayRate);

        exhaustion += amount;
    }

    protected void tick() {
        manaRegen = Math.clamp( manaRegen + getTotalManaRegenRate() / (Math.pow(exhaustion, 0.65) + 1), 0, getTotalMaxManaRegen());
        setMana(mana + manaRegen);

        reduceSpellCooldowns();
        exhaustion = Math.max(0, exhaustion - exhaustionDecayRate);
        exhaustionDecayRate = Math.min(exhaustionDecayRate + 0.001, MAX_EXHAUSTION_DECAY_RATE);
    }

    public void addSpellCooldown(Spell spell, int cooldown) {
        if(spell == null) return;
        String id = spell.getId();

        if(spellCooldowns.containsKey(id) && spellCooldowns.get(id) < cooldown) {
            spellCooldowns.replace(id, cooldown);
            return;
        }
        spellCooldowns.put(id, cooldown);
    }
    private void reduceSpellCooldowns() {
        for(String spell : new HashSet<>(spellCooldowns.keySet()))
        {
            int cooldown = spellCooldowns.get(spell);
            spellCooldowns.replace(spell, cooldown - 1);

            if(cooldown <= 0) spellCooldowns.remove(spell);
        }
        if(globalCooldown > 0) globalCooldown--;
    }

    public int getSpellCooldown(Spell spell) {
        if(spell == null) return -1;
        String id = spell.getId();

        if(!spellCooldowns.containsKey(id)) return -1;

        return spellCooldowns.get(id);
    }

    public void setGlobalCooldown(int ticks) {globalCooldown = ticks;}
}
