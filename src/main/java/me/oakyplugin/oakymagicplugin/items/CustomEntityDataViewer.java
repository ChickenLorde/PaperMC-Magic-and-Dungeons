package me.oakyplugin.oakymagicplugin.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.CustomEntity;
import me.oakyplugin.oakymagicplugin.entity.EntityManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;

public class CustomEntityDataViewer extends Usable {

    public CustomEntityDataViewer() {
     ITEM_MATERIAL = Material.STICK;
     repeatOnHold = false;
    }


    @Override
    public void onUse(Player p, ItemStack item) {
        Location loc = p.getEyeLocation().clone();
        Vector dir = loc.getDirection().clone();

        World w = loc.getWorld();

        for(int i = 0; i < 64; i++) {
            Collection<LivingEntity> entities = w.getNearbyLivingEntities(loc, 0.5);

            LivingEntity closest = null;
            double closestDistance = Double.MAX_VALUE;

            for(LivingEntity e : entities) {
                if(e.equals(p) || !e.isValid()) continue;

                double distance = e.getLocation().distanceSquared(loc);
                if(distance < closestDistance) {
                    closest = e;
                    closestDistance = distance;
                }
            }
            if(closest != null) {
                EntityManager entityManager = OakyMagicPlugin.getPlugin().getEntityManager();

                CustomEntity ce = entityManager.getCustomEntityFromEntity(closest);
                if(ce == null) {
                    p.sendMessage(MiniMessage.miniMessage().deserialize(String.format("<red>%s is not a CustomEntity",closest.getName())));
                    return;
                } else {
                    p.sendMessage(ce.getData());
                    return;
                }
            }
            loc.add(dir);
        }
    }

    @Override
    protected ItemStack customizeItem(ItemStack item) {
        makeUsable(item);
        item.setData(DataComponentTypes.ITEM_NAME, MiniMessage.miniMessage().deserialize("<light_purple>Custom Entity Data Viewer"));
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return item;
    }
}
