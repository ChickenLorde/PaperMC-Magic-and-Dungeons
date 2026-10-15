package me.oakyplugin.oakymagicplugin.status_effect.effects;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.status_effect.StatusEffect;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class Decay extends StatusEffect {

    public final static NamespacedKey modifier = new NamespacedKey(OakyMagicPlugin.NAMESPACE,"status_decay");
    private final double DAMAGE_MULTIPLIER = -0.15;

    public Decay(int duration, UUID target) {
        super("decay", duration, 20, target);
    }

    @Override
    public void onTick() {
        Entity entity = Bukkit.getEntity(getTarget());

        if(entity == null || !entity.isValid() || entity.isDead()) return;

        Particle.FALLING_DUST.builder()
                .location(entity.getLocation().clone().add(0,entity.getBoundingBox().getHeight() * 0.5, 0))
                .offset(entity.getBoundingBox().getWidthX() * 0.4, entity.getBoundingBox().getHeight() * 0.4, entity.getBoundingBox().getWidthZ() * 0.4)
                .count(3)
                .receivers(32)
                .data(BlockType.GRAVEL.createBlockData())
                .spawn();
    }

    @Override
    public void onApply() {
        Entity entity = Bukkit.getEntity(getTarget());
        if(!(entity instanceof  LivingEntity livingEntity)) return;

        AttributeInstance a = livingEntity.getAttribute(Attribute.ATTACK_DAMAGE);
        if(a !=null && a.getModifier(modifier) == null) a.addModifier(new AttributeModifier(modifier,DAMAGE_MULTIPLIER, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    }

    @Override
    public void onExpire() {
        Entity entity = Bukkit.getEntity(getTarget());
        if(!(entity instanceof  LivingEntity livingEntity)) return;

        AttributeInstance a = livingEntity.getAttribute(Attribute.ATTACK_DAMAGE);
        if(a !=null && a.getModifier(modifier) != null) a.removeModifier(modifier);
    }

    @Override
    public void onTrigger() {
        Entity entity = Bukkit.getEntity(getTarget());
        if(!(entity instanceof LivingEntity le)) return;

        le.damage(1, DamageSource.builder(DamageType.WITHER).build());
    }
}
