package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import me.oakyplugin.oakymagicplugin.entity.EntityManager;
import me.oakyplugin.oakymagicplugin.entity.PuppetSpellEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Puppet extends Spell {

    public Puppet() {
        super(new SpellStats(250, 300, 20, 50, 50, false));

        buildLore("\"Create a near identical puppet of yourself using pure magic. Though, it lacks a tangible body and struggles to interact with the physical world\"");
    }
    @Override
    public boolean cast(LivingEntity caster, Location location) {

        if(!(caster instanceof Player p)) return false;

        PuppetSpellEntity pm = new PuppetSpellEntity(p, plugin.getEntityManager());
        Mannequin m = pm.getEntity();
        m.setProfile(ResolvableProfile.resolvableProfile()
                .uuid(caster.getUniqueId())
                .build());
        m.customName(Component.text(caster.getName()));
        m.setCustomNameVisible(true);
        m.setDescription(null);

        return true;
    }

    @Override
    public void chargeTick(Player p, Location location, int chargeTicks) {
        Particle.PORTAL.builder()
                .location(p.getBoundingBox().getCenter().toLocation(p.getWorld()))
                .offset(0.3,0.3,0.3)
                .count(5)
                .extra(2)
                .spawn();
    }

    @Override
    public boolean onChargeStart(Player p, Location location) {
        EntityManager manager = plugin.getEntityManager();
        if(manager.isPlayerPuppet(p)) {
            PuppetSpellEntity pse = manager.getPuppetFromPlayer(p);
            pse.returnToOwner();
            return false;
        }
        return true;
    }

    @Override
    ItemStack getSpellItem(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Puppet"));

        item.lore(getLore());

        return item;
    }
}
