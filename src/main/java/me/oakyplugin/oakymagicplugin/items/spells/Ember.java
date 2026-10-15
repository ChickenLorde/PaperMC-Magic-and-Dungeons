package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.projectiles.LinearProjectile;
import me.oakyplugin.oakymagicplugin.projectiles.ProjectileData;
import me.oakyplugin.oakymagicplugin.status_effect.effects.Burn;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class Ember extends ProjectileSpell {



    public Ember() {
        //1.1
        super(new SpellStats(10, 25, 1.5),
                ProjectileData.builder()
                        .damage(5)
                        .speed(1.2)
                        .maxTicksAlive(25)
                        .collisionSize(0.25)
                        .updatesPerTick(2)
                        .build());

        addStatLore("Damage",getProjectileStats().getDamage(),"red",0);
        buildLore("\"Fire a fast but short-ranged spark that ignites foes\"");
    }

    @Override
    public boolean cast(LivingEntity caster, Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, 0.65f, 2.0f);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_BURN, 0.65f, 2.0f);
        createProjectile(caster, location, location.getDirection());

        return true;
    }

    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Ember"));

        item.lore(getLore());
        return item;
    }

    @Override
    public void onProjectileTick(Location location, LinearProjectile projectile) {
        Particle.FLAME.builder()
                .location(location)
                .offset(0.01, 0.01, 0.01)
                .count(1)
                .extra(0.01)
                .receivers(64, true)
                .spawn();
    }

    @Override
    public void onProjectileEnd(Location location) {
        Particle.FLAME.builder()
                .location(location)
                .offset(0.1, 0.1, 0.1)
                .count(35)
                .extra(0.15)
                .receivers(64, true)
                .spawn();
    }

    @Override
    public void onEntity(LivingEntity cause, LivingEntity target, Location location) {
        int prev = target.getFireTicks();
        target.setFireTicks(1);
        damageEntity(cause, target, getProjectileStats().getDamage(), new Burn(60, target.getUniqueId()));
        target.setFireTicks(prev);

        location.getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT,0.7f, (float)Math.random() * 0.15f + 1.75f);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_BURN,0.7f, (float)Math.random() * 0.15f + 1.75f);
    }
}
