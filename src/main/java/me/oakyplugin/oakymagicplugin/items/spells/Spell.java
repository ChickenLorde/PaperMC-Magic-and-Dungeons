package me.oakyplugin.oakymagicplugin.items.spells;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.EntityManager;
import me.oakyplugin.oakymagicplugin.entity.PuppetSpellEntity;
import me.oakyplugin.oakymagicplugin.entity.SpellDamage;
import me.oakyplugin.oakymagicplugin.items.ItemManager;
import me.oakyplugin.oakymagicplugin.items.Usable;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import me.oakyplugin.oakymagicplugin.status_effect.StatusEffect;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/// ONLY ONE OF EACH SPELL CLASS SHOULD BE INSTANTIATED
@SuppressWarnings("UnstableApiUsage")
public abstract class Spell extends Usable {


    protected final OakyMagicPlugin plugin = OakyMagicPlugin.getPlugin();
    private final SpellStats stats;

    private final List<Component> lore;
    private final List<Component> description;

    public Spell(SpellStats stats) {
        this.stats = stats;
        lore = new ArrayList<>();
        description = new ArrayList<>();
    }

    @Override
    public final void onUse(Player p, ItemStack item) {
        PlayerData data = plugin.getPlayerManager().getPlayerData(p);
        if(p.isSneaking()) return;
        if(data.getStats().getSpellCooldown(this) > 0) {
            if(data.getRightClickDownTime() == 1) p.sendMessage(MiniMessage.miniMessage().deserialize("<red>This spell is still on cooldown"));
            return;
        }
        if(data.getStats().getGlobalCooldown() > 0) {
            if(data.getRightClickDownTime() == 1) p.sendMessage(MiniMessage.miniMessage().deserialize("<red>You are too exhausted to cast any spells"));
            return;
        }
        if(data.getStats().getMana() < stats.manaCost && p.getGameMode() != GameMode.CREATIVE) {
            if(data.getRightClickDownTime() == 1) p.sendMessage(MiniMessage.miniMessage().deserialize("<red>You need "+(int)Math.ceil(stats.manaCost - data.getStats().getMana())+" more mana to cast this spell"));
            return;
        }
        if(!usePrecondition(p)) return;

        if(stats.chargeTicks <= 0) {
            if(cast(p, p.getEyeLocation())) {
                data.getStats().addSpellCooldown(this, stats.cooldown);
                data.getStats().setGlobalCooldown(stats.globalCooldown);
                if (p.getGameMode() != GameMode.CREATIVE)
                    data.getStats().removeMana(stats.manaCost);
                data.getStats().addExhaustion(stats.exhaustion);
                OakyMagicPlugin.getPlugin().getItemManager().resetPlayerCharges(p);
            }
        }
        else
        {

            ItemManager itemManager = plugin.getItemManager();
            Usable usable = itemManager.getUsableFromItem(item);
            int chargeTicks = itemManager.getUsableCharge(usable.getId(),p);

            if(chargeTicks == stats.chargeTicks)
                onMaxCharge(p, p.getLocation());

            if(chargeTicks >= stats.chargeTicks) {
                if(!stats.castOnRelease) {
                    if(cast(p, p.getEyeLocation())) {
                        data.getStats().addSpellCooldown(this, stats.cooldown);
                        data.getStats().setGlobalCooldown(stats.globalCooldown);
                        if (p.getGameMode() != GameMode.CREATIVE)
                            data.getStats().removeMana(stats.manaCost);
                        data.getStats().addExhaustion(stats.exhaustion);
                        OakyMagicPlugin.getPlugin().getItemManager().resetPlayerCharges(p);
                    }
                }
            }
            if(chargeTicks == -1)
                if(!onChargeStart(p, p.getLocation()))
                    itemManager.setUsableCharge(usable.getId(), p, Integer.MIN_VALUE);
            chargeTick(p, p.getLocation(), chargeTicks);

            itemManager.chargeUsable(usable.getId(), p);
        }

    }

    @Override
    public void onRelease(Player p, ItemStack item) {
        if(!stats.castOnRelease) return;
        PlayerData data = plugin.getPlayerManager().getPlayerData(p);
        ItemManager itemManager = plugin.getItemManager();
        Usable usable = itemManager.getUsableFromItem(item);
        int chargeTicks = itemManager.getUsableCharge(usable.getId(),p);

        if(chargeTicks >= stats.chargeTicks) {
            if(cast(p, p.getEyeLocation())) {
                data.getStats().addSpellCooldown(this, stats.cooldown);
                data.getStats().setGlobalCooldown(stats.globalCooldown);
                data.getStats().removeMana(stats.manaCost);
                data.getStats().addExhaustion(stats.exhaustion);
            }
        }
    }

    public abstract boolean cast(LivingEntity caster, Location location);
    public void chargeTick(Player p, Location location, int chargeTicks) {}
    public boolean onChargeStart(Player p, Location location) { return true; }
    public void onMaxCharge(Player p, Location location) {}

    public boolean usePrecondition(Player p) {return true;}

    @Override
    protected ItemStack customizeItem(ItemStack item) {
        makeUsable(item);
        item.setData(DataComponentTypes.ITEM_MODEL, Key.key("minecraft","stick"));
        item.setData(DataComponentTypes.ITEM_NAME, Component.text(getId()));
        return getSpellItem(item);
    }

    abstract ItemStack getSpellItem(ItemStack item);


    public void onEntity(LivingEntity cause, LivingEntity target, Location location) {}

    public void damageEntity(LivingEntity cause, LivingEntity target, double damage) {
        int noDmgTicks = target.getNoDamageTicks();
        target.setNoDamageTicks(0);

        EntityManager eManager = plugin.getEntityManager();

        if(target instanceof Mannequin m) {
            PuppetSpellEntity pse = eManager.getPuppetFromMannequin(m);
            if(pse != null) {
                target = pse.getOwner();
                pse.returnToOwner();
            }
        }


        new SpellDamage(damage, target, cause, this, 0);

        target.setNoDamageTicks(noDmgTicks);
    }
    public void damageEntity(LivingEntity cause, LivingEntity target, double damage, StatusEffect effect) {
        int noDmgTicks = target.getNoDamageTicks();
        target.setNoDamageTicks(0);

        if(target instanceof Mannequin m) {
            PuppetSpellEntity pse = plugin.getEntityManager().getPuppetFromMannequin(m);
            if(pse != null) {
                target = pse.getOwner();
                pse.returnToOwner();
            }
        }
        effect.setTarget(target.getUniqueId());
        plugin.getStatusEffectManager().inflict(target.getUniqueId(), effect);
        new SpellDamage(damage, target, cause, this, 0);

        target.setNoDamageTicks(noDmgTicks);
    }

    public SpellStats getStats() {return stats;}

    public void addStatLore(String stat, double value, String color) {
        Component text = null;
        if(value % 1 == 0)
            text = MiniMessage.miniMessage().deserialize(String.format("<italic:false><gray>%s: <%s>%d",stat,color,(int)value));
        else
            text = MiniMessage.miniMessage().deserialize(String.format("<italic:false><gray>%s: <%s>%.2f",stat,color,value));
        lore.add(text);
    }
    public void addStatLore(String stat, String s, String color) {
        Component text = null;
        text = MiniMessage.miniMessage().deserialize(String.format("<italic:false><gray>%s: <%s>%s",stat,color,s));
        lore.add(text);
    }
    public void addStatLore(String stat, double value, String color, String suffix) {
        Component text = null;
        if(value % 1 == 0)
            text = MiniMessage.miniMessage().deserialize(String.format("<italic:false><gray>%s: <%s>%d%s",stat,color,(int)value,suffix));
        else
            text = MiniMessage.miniMessage().deserialize(String.format("<italic:false><gray>%s: <%s>%.2f%s",stat,color,value,suffix));
        lore.add(text);
    }
    public void addStatLore(String stat, double value, String color, int index) {
        Component text = null;
        if(value % 1 == 0)
            text = MiniMessage.miniMessage().deserialize(String.format("<italic:false><gray>%s: <%s>%d",stat,color,(int)value));
        else
            text = MiniMessage.miniMessage().deserialize(String.format("<italic:false><gray>%s: <%s>%.2f",stat,color,value));
        lore.add(index,text);
    }

    public void createBasicStatLore() {
        addStatLore("Mana Cost", stats.manaCost,"aqua");
        if(stats.exhaustion > 0)
            addStatLore("Exhaustion", stats.exhaustion,"dark_purple");
        if(stats.chargeTicks > 0)
            addStatLore("Charge Time", stats.chargeTicks * 0.05,"yellow", "s");
        addStatLore("Cooldown", stats.cooldown * 0.05,"green", "s");
        if(stats.globalCooldown != stats.cooldown)
            addStatLore("Global Cooldown", stats.globalCooldown * 0.05,"green", "s");
    }

    public void setDescription(String s) {
        List<Component> componentLines = new ArrayList<>();
        String[] words = s.split(" ");
        final int lineLength = 33;
        StringBuilder line = new StringBuilder();

        for(String w : words) {
            line.append(w);
            line.append(" ");
            if(line.length() > lineLength) {
                line.insert(0, "<dark_gray><italic:false>");
                componentLines.add(MiniMessage.miniMessage().deserialize(line.toString()));
                line = new StringBuilder();
            }
        }
        if(!line.isEmpty())
        {
            line.insert(0, "<dark_gray><italic:false>");
            componentLines.add(MiniMessage.miniMessage().deserialize(line.toString()));
        }
        description.addAll(componentLines);
    }

    public void buildLore(String description) {
        createBasicStatLore();
        setDescription(description);
        lore.addAll(this.description);
    }

    public List<Component> getLore() {return lore;}

    protected void damage(LivingEntity e, double amount, LivingEntity cause) {
        e.setNoDamageTicks(0);

        e.damage(0, DamageSource.builder(DamageType.MAGIC)
                        .withCausingEntity(cause)
                        .withDamageLocation(cause.getLocation())
                        .build());
        double newHealth = e.getHealth() - amount;
        e.setHealth(newHealth);
    }


    public record SpellStats(double manaCost, int cooldown, int globalCooldown, int chargeTicks, double exhaustion, boolean castOnRelease) {

        SpellStats(double manaCost, int cooldown, double exhaustion) {
            this(manaCost, cooldown, cooldown, 0, exhaustion, false);
        }
        SpellStats(double manaCost, int cooldown, int chargeTicks, double exhaustion) {
            this(manaCost, cooldown, cooldown, chargeTicks, exhaustion, false);
        }
        SpellStats(double manaCost, int cooldown, int chargeTicks, double exhaustion, boolean castOnRelease) {
            this(manaCost, cooldown, cooldown, chargeTicks, exhaustion, castOnRelease);
        }
    }



}
