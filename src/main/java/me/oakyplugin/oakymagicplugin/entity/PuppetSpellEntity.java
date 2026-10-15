package me.oakyplugin.oakymagicplugin.entity;

import io.papermc.paper.entity.TeleportFlag;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PuppetSpellEntity extends CustomEntity {

    private final Player owner;

    private final double originalHP, originalHunger, originalAbsorption, originalSaturation;
    private final int originalAir;

    private double puppetHP;

    private ItemStack[] inventory;
    private ItemStack[] armor;
    private ItemStack[] extra;

    private Chunk currentChunk;

    private boolean returnedToOwner;

    public PuppetSpellEntity(Player owner, EntityManager manager) {
        super(Mannequin.class, manager);
        this.owner = owner;
        manager.registerPuppet(owner, this);

        originalHP = owner.getHealth();
        originalHunger = owner.getFoodLevel();
        originalAbsorption = owner.getAbsorptionAmount();
        originalSaturation = owner.getSaturation();
        originalAir = owner.getRemainingAir();

        puppetHP = 10;
        returnedToOwner = false;

        spawn(owner.getLocation());

        copyOwner();
        getEntity().setPersistent(true);
        getEntity().getChunk().load(true);
        getEntity().getAttribute(Attribute.MAX_HEALTH).setBaseValue(1024);
        getEntity().setHealth(1024);
    }


    public void copyOwner() {
        Mannequin m = getEntity();

        Location originalLoc = getEntity().getLocation().clone();

        m.teleport(owner.getLocation());
        owner.teleport(originalLoc);

        m.setMainHand(owner.getMainHand());

        m.getEquipment().setArmorContents(owner.getEquipment().getArmorContents());
        m.getEquipment().setItemInMainHand(owner.getEquipment().getItemInMainHand());
        m.getEquipment().setItemInOffHand(owner.getEquipment().getItemInOffHand());
        m.setRemainingAir(originalAir);
        currentChunk = m.getChunk();
        currentChunk.setForceLoaded(true);

        owner.setHealth(puppetHP);
        owner.setAbsorptionAmount(0);
        owner.setFoodLevel(20);
        owner.setSaturation(20);
        owner.setRemainingAir(owner.getMaximumAir());

        inventory = cloneContents(owner.getInventory().getContents());
        armor = cloneContents(owner.getInventory().getArmorContents());
        extra = cloneContents(owner.getInventory().getExtraContents());

    }

    public void returnToOwner() {
        if(returnedToOwner) return;
        returnedToOwner = true;

        currentChunk.setForceLoaded(false);

        Particle.LARGE_SMOKE.builder()
                .location(owner.getBoundingBox().getCenter().toLocation(owner.getWorld()))
                .count(200)
                .extra(0.1)
                .spawn();

        owner.setHealth(originalHP);
        owner.setFoodLevel((int)originalHunger);
        owner.setAbsorptionAmount(originalAbsorption);
        owner.setSaturation((float)originalSaturation);
        owner.setRemainingAir(getEntity().getRemainingAir());

        if(entityExists()) {
            if(owner.getWorld().equals(getEntity().getWorld())) {
                Interaction inte = getEntity().getWorld().spawn(getEntity().getLocation(), Interaction.class);
                inte.addPassenger(owner);
                inte.remove();
            }
            else {
                owner.teleport(getEntity().getLocation());
            }
            owner.setVelocity(getEntity().getVelocity());
            getEntity().remove();
        }
        else {
            owner.teleportAsync(getSpawnLocation(), TeleportFlag.Relative.VELOCITY_X, TeleportFlag.Relative.VELOCITY_Y, TeleportFlag.Relative.VELOCITY_Z);
        }

        owner.getInventory().setContents(inventory);
        owner.getInventory().setArmorContents(armor);
        owner.getInventory().setExtraContents(extra);
        owner.updateInventory();

        OakyMagicPlugin.getPlugin().getStatusEffectManager().clearStatusEffects(owner.getUniqueId());
        owner.setVisualFire(TriState.FALSE);

        entityManager.updateEntities();
    }

    private ItemStack[] cloneContents(ItemStack[] contents) {
        ItemStack[] clone = new ItemStack[contents.length];

        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                clone[i] = contents[i].clone();
            }
        }

        return clone;
    }

    public void updateChunk() {
        Chunk c = getEntity().getChunk();
        if(!c.equals(currentChunk)) {
            currentChunk.setForceLoaded(false);
            c.setForceLoaded(true);
        }

    }

    @Override
    public Mannequin getEntity() {
        return (Mannequin)super.getEntity();
    }

    public Player getOwner() {return owner;}

    public double getPuppetHP() {return puppetHP;}
    public void damagePuppet(double amount) { puppetHP -= amount;}
    public Chunk getCurrentChunk() {return currentChunk;}
}
