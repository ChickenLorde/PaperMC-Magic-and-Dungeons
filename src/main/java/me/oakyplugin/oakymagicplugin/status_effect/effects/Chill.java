package me.oakyplugin.oakymagicplugin.status_effect.effects;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.status_effect.StatusEffect;
import org.bukkit.Bukkit;
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

public class Chill extends StatusEffect {

    public final static NamespacedKey modifier = new NamespacedKey(OakyMagicPlugin.NAMESPACE,"status_chill");;
    private final double SPEED_MULTIPLIER = -0.25;

    public Chill(int duration, UUID target) {
        super("chill", duration, 30, target);
    }

    @Override
    public void onTick() {
        Entity entity = Bukkit.getEntity(getTarget());

        if(entity == null || !entity.isValid() || entity.isDead()) return;

        Particle.SNOWFLAKE.builder()
                .location(entity.getLocation().clone().add(0,entity.getBoundingBox().getHeight() * 0.5, 0))
                .offset(entity.getBoundingBox().getWidthX() * 0.167, entity.getBoundingBox().getHeight() * 0.167, entity.getBoundingBox().getWidthZ() * 0.167)
                .count(3)
                .receivers(32)
                .extra(0.07)
                .spawn();

        Particle.BLOCK.builder()
                .location(entity.getLocation().clone().add(0,entity.getBoundingBox().getHeight() * 0.5, 0))
                .offset(entity.getBoundingBox().getWidthX() * 0.167, entity.getBoundingBox().getHeight() * 0.167, entity.getBoundingBox().getWidthZ() * 0.167)
                .count(2)
                .receivers(32)
                .extra(0.1)
                .data(BlockType.BLUE_ICE.createBlockData())
                .spawn();
    }

    @Override
    public void onApply() {
        Entity entity = Bukkit.getEntity(getTarget());
        if(!(entity instanceof  LivingEntity livingEntity)) return;

        AttributeInstance a = livingEntity.getAttribute(Attribute.MOVEMENT_SPEED);
        if(a !=null && a.getModifier(modifier) == null) a.addModifier(new AttributeModifier(modifier,SPEED_MULTIPLIER, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    }

    @Override
    public void onExpire() {
        Entity entity = Bukkit.getEntity(getTarget());
        if(!(entity instanceof  LivingEntity livingEntity)) return;

        AttributeInstance a = livingEntity.getAttribute(Attribute.MOVEMENT_SPEED);
        if(a !=null && a.getModifier(modifier) != null) a.removeModifier(modifier);
    }

    @Override
    public void onTrigger() {
        Entity entity = Bukkit.getEntity(getTarget());
        if(!(entity instanceof LivingEntity le)) return;

        le.damage(1, DamageSource.builder(DamageType.FREEZE).build());
    }
}
