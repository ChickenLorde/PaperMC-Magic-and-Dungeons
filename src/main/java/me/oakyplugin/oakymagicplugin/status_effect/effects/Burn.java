package me.oakyplugin.oakymagicplugin.status_effect.effects;

import me.oakyplugin.oakymagicplugin.status_effect.StatusEffect;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class Burn extends StatusEffect {

    public Burn(int duration, UUID target) {
        super("burn", duration, 20, target);
    }

    @Override
    public void onTick() {
        Entity entity = Bukkit.getEntity(getTarget());
        if(entity == null) return;

        entity.setVisualFire(TriState.FALSE);
        if(!entity.isDead()) entity.setVisualFire(TriState.TRUE);
        if(entity.isInWater()) end();
    }

    @Override
    public void onApply() {
        Entity entity = Bukkit.getEntity(getTarget());
        if(entity != null)
            entity.setVisualFire(TriState.TRUE);
    }

    @Override
    public void onExpire() {
        Entity entity = Bukkit.getEntity(getTarget());
        if(entity != null)
            entity.setVisualFire(TriState.FALSE);
    }

    @Override
    public void onTrigger() {
        Entity entity = Bukkit.getEntity(getTarget());
        if(!(entity instanceof LivingEntity le)) return;

        int prev = le.getFireTicks();
        le.setFireTicks(1);

        le.damage(1, DamageSource.builder(DamageType.ON_FIRE).build());
        le.setFireTicks(prev);
    }
}
