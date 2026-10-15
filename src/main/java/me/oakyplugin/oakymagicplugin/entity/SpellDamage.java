package me.oakyplugin.oakymagicplugin.entity;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import org.bukkit.entity.LivingEntity;

public class SpellDamage {

    public enum DamageType {
        ARCANE
    }

    private double damage;
    private LivingEntity source;
    private LivingEntity target;
    private Spell spell;

    private double magicArmorPenetration;
    private String deathMessage;

    public SpellDamage(double damage, LivingEntity target, LivingEntity source, Spell spell, double magicArmorPenetration) {
        this.damage = damage;
        this.source = source;
        this.spell = spell;
        this.target = target;
        this.magicArmorPenetration = magicArmorPenetration;

        if(source != null)
            deathMessage = String.format("%s was slain by %s's %s",target.getName(),source.getName(),spell.getIdFormatted());
        else
            deathMessage = String.format("%s was slain by %s",target.getName(),spell.getIdFormatted());

        EntityManager manager = OakyMagicPlugin.getPlugin().getEntityManager();
        manager.damageEntity(this);
    }

    public LivingEntity getTarget() {return target;}

    public double getDamage() {
        return damage;
    }

    public LivingEntity getSource() {
        return source;
    }

    public Spell getSpell() {
        return spell;
    }

    public double getMagicArmorPenetration() {
        return magicArmorPenetration;
    }

    public String getDeathMessage() {
        return deathMessage;
    }
}
