package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.projectiles.LinearProjectile;
import me.oakyplugin.oakymagicplugin.projectiles.ProjectileData;
import me.oakyplugin.oakymagicplugin.status_effect.effects.Chill;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

public class Icicle extends ProjectileSpell {

    public Icicle() {
        super(new SpellStats(12, 30, 2), ProjectileData.builder()
                .damage(6)
                .speed(0.8)
                .maxTicksAlive(50)
                .collisionSize(0.33)
                .updatesPerTick(2)
                .build());

        addStatLore("Damage",6,"red",0);
        buildLore("\"Hurl a slow, heavy-hitting chunk of ice at enemies, slowing them for a time upon contact\"");
    }

    @Override
    public boolean cast(LivingEntity caster, Location location) {
        LinearProjectile projectile = createProjectile(caster, location, location.getDirection());
        ItemDisplay display = location.getWorld().createEntity(location, ItemDisplay.class);
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(0.25);

        display.setTransformation(transformation);
        display.setItemStack(new ItemStack(Material.PACKED_ICE));
        display.setTeleportDuration(1);

        projectile.setVisualDisplay(display);
        return true;
    }

    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Icicle"));

        item.lore(getLore());
        return item;
    }

    @Override
    public void onProjectileTick(Location location, LinearProjectile projectile) {
        Particle.SNOWFLAKE.builder()
                .location(location)
                .offset(0.02, 0.02, 0.02)
                .count(1)
                .extra(0.02)
                .receivers(64, true)
                .spawn();
        Particle.BLOCK.builder()
                .location(location)
                .offset(0.02, 0.02, 0.02)
                .count(1)
                .extra(0.04)
                .receivers(64, true)
                .data(BlockType.BLUE_ICE.createBlockData())
                .spawn();
    }

    @Override
    public void onProjectileEnd(Location location) {
        Particle.SNOWFLAKE.builder()
                .location(location)
                .offset(0.1, 0.1, 0.1)
                .count(20)
                .extra(0.15)
                .receivers(64, true)
                .spawn();
        Particle.BLOCK.builder()
                .location(location)
                .offset(0.1, 0.1, 0.1)
                .count(15)
                .extra(0.25)
                .receivers(64, true)
                .data(BlockType.BLUE_ICE.createBlockData())
                .spawn();
        location.getWorld().playSound(location, Sound.BLOCK_GLASS_BREAK,0.7f, (float)Math.random() * 0.15f + 1.75f);
    }

    @Override
    public void onEntity(LivingEntity cause, LivingEntity target, Location location) {
        damageEntity(cause, target, getProjectileStats().getDamage(), new Chill(60, target.getUniqueId()));
    }
}
