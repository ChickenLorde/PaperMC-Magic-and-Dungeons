package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.projectiles.LinearProjectile;
import me.oakyplugin.oakymagicplugin.projectiles.ProjectileData;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class ManaBolt extends ProjectileSpell {
    public ManaBolt() {
        //1.1
        super(new SpellStats(8, 20, 1.5),
                ProjectileData.builder()
                .damage(5)
                .speed(1)
                .maxTicksAlive(30)
                .collisionSize(0.25)
                .updatesPerTick(2)
                .build());
        addStatLore("Damage",getProjectileStats().getDamage(),"red",0);
        buildLore("\"The simplest of spells that anyone can use without even having to learn it. Fire a small ball of mana that travels slowly and damages targets upon contact\"");
    }

    @Override
    public boolean cast(LivingEntity caster, Location location) {

        location.getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, 0.65f, 2.0f);
        LinearProjectile projectile = createProjectile(caster, location, location.getDirection());
        return true;
    }

    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Mana Bolt"));

        item.lore(getLore());

        return item;
    }

    @Override
    public void onProjectileTick(Location location, LinearProjectile projectile) {
        Particle.FIREWORK.builder()
                .location(location)
                .offset(0.02, 0.02, 0.02)
                .count(1)
                .extra(0.02)
                .receivers(64, true)
                .spawn();
    }

    @Override
    public void onProjectileEnd(Location location) {
        Particle.FIREWORK.builder()
                .location(location)
                .offset(0.1, 0.1, 0.1)
                .count(15)
                .extra(0.1)
                .receivers(64, true)
                .spawn();
    }

    @Override
    public void onEntity(LivingEntity cause, LivingEntity target, Location location) {
        damageEntity(cause, target, getProjectileStats().getDamage());

        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_BURN,0.7f, (float)Math.random() * 0.15f + 1.75f);

    }
}
