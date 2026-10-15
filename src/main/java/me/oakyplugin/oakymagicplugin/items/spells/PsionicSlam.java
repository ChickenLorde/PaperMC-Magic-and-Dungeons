package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PsionicSlam extends Spell {

    private final HashMap<LivingEntity, Integer> slamStage;

    public PsionicSlam() {
        super(new SpellStats(30, 160, 30, 20, 6, false));
        slamStage = new HashMap<>();
    }

    @Override
    public boolean cast(LivingEntity caster, Location location) {
         caster.sendMessage(Component.text("Hi, I do nothing right now! :D"));
         return true;
    }

    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Psionic Slam"));

        item.lore(getLore());

        return item;
    }
}
