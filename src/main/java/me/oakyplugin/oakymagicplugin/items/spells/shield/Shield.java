package me.oakyplugin.oakymagicplugin.items.spells.shield;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;

public class Shield {

    private boolean isActive;
    private Player owner;
    private PlayerData ownerData;

    private ItemDisplay display;
    private Location loc;
    private double scale;

    private double shieldRadius;

    public Shield(Player p, ShieldManager shieldManager) {
        owner = p;
        this.loc = p.getEyeLocation().clone().add(p.getEyeLocation().getDirection().multiply(1.5));
        isActive = true;
        scale = 0;
        shieldManager.addShield(this);

        display = p.getWorld().createEntity(p.getEyeLocation().clone().add(owner.getEyeLocation().getDirection().multiply(2)), ItemDisplay.class);
        display.setTeleportDuration(1);
        display.setInterpolationDuration(1);
        display.getPersistentDataContainer().set(OakyMagicPlugin.REMOVE_ON_DISABLE, PersistentDataType.BOOLEAN, true);
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString("bush_magic_shield_panel"));
        display.setItemStack(item);
        display.setShadowStrength(0);

        Transformation t = display.getTransformation();
        t.getScale().set(0);
        display.setTransformation(t);

        display.spawnAt(p.getEyeLocation().clone().add(owner.getEyeLocation().getDirection().multiply(2)));

        shieldRadius = 1.25;

        ownerData = OakyMagicPlugin.getPlugin().getPlayerManager().getPlayerData(owner);

    }

    protected void tick() {
        if((owner == null || !owner.isSneaking() || !owner.isValid() || (!ownerData.isRightClicking() && !owner.getScoreboardTags().contains("force_shield_up")) || ownerData.getStats().getMana() < 1) && isActive)
        {
            isActive = false;
            display.remove();
            display = null;
            return;
        }
        if(owner == null) return;

        ownerData.getStats().removeMana(0.25);
        ownerData.getStats().addExhaustion(0.01);

        scale += (shieldRadius - scale) * 0.3;
        Transformation t = display.getTransformation();
        t.getScale().set(scale);
        display.setTransformation(t);

        loc = owner.getEyeLocation().clone().add(owner.getEyeLocation().getDirection().multiply(1.5));
        display.teleport(loc);

    }

    public boolean isActive() {return isActive;}
    public Player getOwner() {return owner;}

    public Location getLocation() {return loc;}
    public double getRadius() {return shieldRadius;}
}
