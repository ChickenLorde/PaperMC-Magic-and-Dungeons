package me.oakyplugin.oakymagicplugin.player;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.MagicStats;
import me.oakyplugin.oakymagicplugin.items.accessories.Accessory;
import me.oakyplugin.oakymagicplugin.items.armor.ArmorPiece;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import me.oakyplugin.oakymagicplugin.items.spells.shield.Shield;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import me.oakyplugin.oakymagicplugin.player.data.PlayerStats;
import me.oakyplugin.oakymagicplugin.items.Usable;
import me.oakyplugin.oakymagicplugin.player.data.StatBonus;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerTick {
    private final OakyMagicPlugin plugin;

    public PlayerTick(OakyMagicPlugin plugin) {
        this.plugin = plugin;

    }
    public void tick(Player p) {
        PlayerData data = plugin.getPlayerManager().getPlayerData(p);

        calculateBonuses(p, data);

        String actionBar = "";

        double mana = data.getStats().getMana();
        double maxMana = data.getStats().getTotalMaxMana();
        if(mana < maxMana || data.isHoldingSpell())
            actionBar += displayMana(p, data);

        if(data.canInteract()) {
            if (data.isRightClicking()) {
                ItemStack item = p.getInventory().getItemInMainHand();
                Usable usable = plugin.getItemManager().getUsableFromItem(item);

                if (usable != null && (usable.repeatOnHold() || data.getRightClickDownTime() == 1)) {
                    usable.onUse(p, item);
                }
            } else {
                if (data.getRightClickUpTime() == 1) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    Usable usable = plugin.getItemManager().getUsableFromItem(item);

                    if (usable != null)
                        usable.onRelease(p, item);
                }
                plugin.getItemManager().resetPlayerCharges(p);
            }

            if ((data.getRightClickDownTime() == 4 || p.getScoreboardTags().contains("force_shield_up")) && p.isSneaking() && !plugin.getShieldManager().playerHasShield(p)) {
                if (data.getStats().getMana() < 5)
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<red>You need at least 5 mana to conjure a shield"));
                new Shield(p, plugin.getShieldManager());
            }
        }

        String cooldown = displayCooldowns(p, data);
        if(!cooldown.isEmpty()) {
            if(!actionBar.isEmpty())
                actionBar += " <gray>| "+cooldown;
            else
                actionBar += cooldown;
        }

        String exhaustion = displayExhaustion(p, data);
        if(!exhaustion.isEmpty()) {
            if(!actionBar.isEmpty())
                actionBar += " <gray>| "+exhaustion;
            else
                actionBar += exhaustion;
        }

        if(OakyMagicPlugin.getPlugin().getEntityManager().isPlayerPuppet(p)) {
            if(!actionBar.isEmpty())
                actionBar += " <gray>| <red>PUPPET";
            else
                actionBar += " <red>PUPPET";
        }


        if(!actionBar.isEmpty())
            p.sendActionBar(MiniMessage.miniMessage().deserialize(actionBar));
    }

    public String displayMana(Player p, PlayerData data) {
        double mana = data.getStats().getMana();
        double maxMana = data.getStats().getTotalMaxMana();

        String color = "<aqua>";
        Spell heldSpell = data.getHeldSpell();
        if(heldSpell != null && data.getStats().getMana() < heldSpell.getStats().manaCost())
                color = "<red>";

        return color+"Mana: "+(int)mana+"/"+(int)maxMana;
    }

    private String displayCooldowns(Player p, PlayerData data) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if(item.getType().isAir() || item.isEmpty()) return "";

        Usable usable = plugin.getItemManager().getUsableFromItem(item);
        if(!(usable instanceof Spell spell)) return "";

        double cooldown;
        boolean displayLocal = data.getStats().getSpellCooldown(spell) >= data.getStats().getGlobalCooldown();

        if(displayLocal)
            cooldown = data.getStats().getSpellCooldown(spell);
        else
            cooldown = data.getStats().getGlobalCooldown();

        if(cooldown <= 0) return "";


        String color = displayLocal ? "<green>" : "<gray>";
        return color+String.format("Cooldown: %.1fs",cooldown / 20.0);

    }

    private String displayExhaustion(Player p, PlayerData data) {
        double exhaustion = data.getStats().getExhaustion();
        if(exhaustion == 0) return "";

        String color = "<dark_purple>";

        return color+"Exhaustion: "+(int)Math.ceil(exhaustion);
    }

    private void calculateBonuses(Player p, PlayerData data) {
        double maxMana = 0,
        maxManaRegen = 0,
        manaRegenRate = 0,
        damageMultiplier = 0,
        magicArmor = 0;

        for(Accessory acc : data.getAccessories().getAccessories()) {
            StatBonus bonuses = acc.getStatBonus();
            if(bonuses == null) continue;
            MagicStats magicStats = bonuses.getMagicStats();

            maxMana += bonuses.getMaxManaBonus();
            maxManaRegen += bonuses.getMaxManaRegenBonus();
            manaRegenRate += bonuses.getManaRegenRateBonus();
            damageMultiplier += magicStats.getTotalDamageMultiplier();
            magicArmor += magicStats.getTotalMagicArmor();
        }
        for(ItemStack item : p.getEquipment().getArmorContents()) {
            ArmorPiece armor = plugin.getItemManager().getCustomItemFromItem(item, ArmorPiece.class);
            if(armor == null) continue;

            StatBonus bonuses = armor.getStatBonus();
            if(bonuses == null) continue;
            MagicStats magicStats = bonuses.getMagicStats();

            maxMana += bonuses.getMaxManaBonus();
            maxManaRegen += bonuses.getMaxManaRegenBonus();
            manaRegenRate += bonuses.getManaRegenRateBonus();
            damageMultiplier += magicStats.getTotalDamageMultiplier();
            magicArmor += magicStats.getTotalMagicArmor();

        }

        PlayerStats stats = data.getStats();
        MagicStats magicStats = stats.getMagicStats();

        stats.setExtraMaxMana(maxMana);
        stats.setExtraMaxManaRegen(maxManaRegen);
        stats.setExtraManaRegenRate(manaRegenRate);
        magicStats.setExtraDamageMultiplier(damageMultiplier);
        magicStats.setExtraMagicArmor(magicArmor);
    }
}
