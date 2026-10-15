package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.projectiles.LinearProjectile;
import me.oakyplugin.oakymagicplugin.projectiles.ProjectileData;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

public class ArcaneBlast extends ProjectileSpell {

    public ArcaneBlast() {
        super(new SpellStats(40, 30, 30, 40, 15, true),
                ProjectileData.builder()
                        .damage(25)
                        .speed(2.5)
                        .maxTicksAlive(30)
                        .collisionSize(0.66)
                        .updatesPerTick(10)
                        .build());

        addStatLore("Damage",25,"red",0);
        buildLore("\"Concentrate mana into a projectile of terrifying power\"");
    }
    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Arcane Blast"));
        item.lore(getLore());
        return item;
    }

    @Override
    public void chargeTick(Player p, Location location, int chargeTicks) {
        if(chargeTicks >= getStats().chargeTicks()) {
            Particle.FIREWORK.builder()
                    .location(p.getBoundingBox().getCenter().toLocation(p.getWorld()))
                    .count(1)
                    .extra(0.12)
                    .spawn();
        }
        Particle.FIREWORK.builder()
                .location(p.getBoundingBox().getCenter().toLocation(p.getWorld()))
                .count(1)
                .extra(0.02)
                .spawn();
    }

    @Override
    public void onMaxCharge(Player p, Location location) {
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 2.0f);
        Particle.END_ROD.builder()
                .location(p.getBoundingBox().getCenter().toLocation(p.getWorld()))
                .count(25)
                .extra(0.08)
                .spawn();
    }

    @Override
    public void onProjectileTick(Location location, LinearProjectile projectile) {


        double radius = Math.min(0.25,projectile.getUpdateTicksActive() * 0.01);
        double a = projectile.getUpdateTicksActive() * 0.2;

        Vector dir = projectile.getDir();
        Vector up = new Vector(0, 1, 0);

        if (Math.abs(dir.dot(up)) > 0.99) {
            up = new Vector(1, 0, 0);
        }
        Vector right = dir.clone().crossProduct(up).normalize();
        Vector forward = right.clone().crossProduct(dir).normalize();

        int k = 2;
        for(int i = 0; i < k; i ++) {
            Vector offset = right.clone().multiply(Math.cos(a + i * 2 * Math.PI / k) * radius)
                    .add(forward.clone().multiply(Math.sin(a + i * 2 * Math.PI / k) * radius));


            Location particlePos = location.clone().add(offset);

            Particle.END_ROD.builder()
                    .location(particlePos)
                    .extra(0)
                    .count(1)
                    .spawn();
        }

    }

    @Override
    public void onProjectileEnd(Location location) {
        Particle.END_ROD.builder()
                .location(location)
                .offset(0.07, 0.07, 0.07)
                .count(80)
                .extra(0.15)
                .receivers(64, true)
                .spawn();
        Particle.EXPLOSION.builder()
                .location(location)
                .count(1)
                .receivers(64, true)
                .spawn();
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.75f, 2.0f);
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 1, 2);
    }

    @Override
    public boolean cast(LivingEntity caster, Location location) {
        LinearProjectile projectile = createProjectile(caster, location, location.getDirection());
        ItemDisplay display = location.getWorld().createEntity(location, ItemDisplay.class);
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(0.5);

        display.setTransformation(transformation);
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                        .addString("bush_arcane_blast")
                .build());
        display.setItemStack(item);
        display.setBillboard(Display.Billboard.CENTER);
        display.setBrightness(new Display.Brightness(15, 15));
        display.setTeleportDuration(1);
        display.getPersistentDataContainer().set(OakyMagicPlugin.REMOVE_ON_DISABLE, PersistentDataType.BOOLEAN, true);

        projectile.setVisualDisplay(display);

        location.getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1, 2);
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, 1, 2);

        Vector dir = caster.getEyeLocation().getDirection();
        Vector up = new Vector(0, 1, 0);

        if (Math.abs(dir.dot(up)) > 0.99) {
            up = new Vector(1, 0, 0);
        }
        Vector right = dir.clone().crossProduct(up).normalize();
        Vector forward = right.clone().crossProduct(dir).normalize();

        int angles = 12;
        for(int i = 0; i < angles; i++) {
            double angle = i * 2 * Math.PI / angles;
            Vector offset = right.clone().multiply(Math.cos(angle) * 0.1).add(forward.clone().multiply(Math.sin(angle) * 0.1));

            Particle.END_ROD.builder()
                    .location(caster.getEyeLocation().clone().add(dir.clone().multiply(0.75)))
                    .count(0)
                    .extra(1)
                    .offset(offset.getX(), offset.getY(), offset.getZ())
                    .spawn();
        }
        angles = 24;
        for(int i = 0; i < angles; i++) {
            double angle = i * 2 * Math.PI / angles;
            Vector offset = right.clone().multiply(Math.cos(angle) * 0.2).add(forward.clone().multiply(Math.sin(angle) * 0.2));

            Particle.END_ROD.builder()
                    .location(caster.getEyeLocation().clone().add(dir.clone().multiply(0.75)))
                    .count(0)
                    .extra(1)
                    .offset(offset.getX(), offset.getY(), offset.getZ())
                    .spawn();
        }
        return true;
    }

    @Override
    public void onEntity(LivingEntity cause, LivingEntity target, Location location) {
        damageEntity(cause, target, getProjectileStats().getDamage());
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_BURN,0.7f, (float)Math.random() * 0.15f + 1.75f);
    }
}
