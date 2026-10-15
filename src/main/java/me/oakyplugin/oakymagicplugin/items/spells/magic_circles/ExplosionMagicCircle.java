package me.oakyplugin.oakymagicplugin.items.spells.magic_circles;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;

public class ExplosionMagicCircle extends MagicCircle {
    private double scale;
    private double targetScale;
    private double rotation;

    public ExplosionMagicCircle(Player owner, Location location, Spell spell, double scale, int delay) {
        super(owner, location, spell, delay);
        targetScale = scale;

        itemDisplay = location.getWorld().createEntity(location, ItemDisplay.class);
        itemDisplay.setRotation(0,0);
        Transformation transf = itemDisplay.getTransformation();
        transf.getScale().set(0.001);
        itemDisplay.setTransformation(transf);

        itemDisplay.setBrightness(new Display.Brightness(15, 15));
        itemDisplay.setInterpolationDuration(2);
        itemDisplay.setTeleportDuration(1);
        itemDisplay.setViewRange(2);
        itemDisplay.setShadowStrength(0);

        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString("bush_megumin_explosion_ring")
                .build());

        itemDisplay.setItemStack(item);


        itemDisplay.spawnAt(location);
        itemDisplay.getPersistentDataContainer().set(OakyMagicPlugin.REMOVE_ON_DISABLE, PersistentDataType.BOOLEAN, true);
    }

    @Override
    protected void tickExtra() {
        if(!isDespawning()) scale += (targetScale - scale) / 8;

        rotation += 28.0 / (0.0156 * scale * scale + 0.5);


        if(itemDisplay != null) {
            itemDisplay.setRotation((float)rotation, 0);
            Transformation t = itemDisplay.getTransformation();
            t.getScale().set(scale);
            itemDisplay.setTransformation(t);
        }

        PlayerData data = OakyMagicPlugin.getPlugin().getPlayerManager().getPlayerData(getOwner());

        if(!data.isRightClicking() && !isDespawning())
        {
            startDespawn();
            itemDisplay.setInterpolationDuration(2);
        }

    }

    @Override
    protected void whileDespawn() {
        scale += (0.001 - scale) / 3;

        Transformation t = itemDisplay.getTransformation();
        t.getScale().set(scale);
        itemDisplay.setTransformation(t);

        if(scale < 0.005) remove();
    }
}
