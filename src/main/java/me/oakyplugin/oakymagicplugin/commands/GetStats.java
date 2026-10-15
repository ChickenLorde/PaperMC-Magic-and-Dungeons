package me.oakyplugin.oakymagicplugin.commands;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.player.data.PlayerStats;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetStats implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player p)) return false;

        Player target;

        if(strings.length > 0) {
            String name = strings[0];

            Player player = Bukkit.getPlayer(name);
            if(player == null) {
                commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Player not found"));
                return true;
            } else
                target = player;
        } else
            target = p;


        OakyMagicPlugin plugin = OakyMagicPlugin.getPlugin();
        PlayerStats stats = plugin.getPlayerManager().getPlayerData(target).getStats();
        target.sendMessage(MiniMessage.miniMessage().deserialize(String.format("<gold>---===<%s's Stats>===---",target.getName())));

        displayMaxMana(target, stats);
        displayMaxManaRegen(target, stats);
        displayManaRegenRate(target, stats);
        displayDamageMultiplier(target, stats);
        displayMagicArmor(target, stats);
        return true;

    }

    private void displayMaxMana(Player p, PlayerStats stats) {
        String maxMana = String.format("<gray>|  Max Mana: <aqua>%d", (int)stats.getTotalMaxMana());
        if(stats.getExtraMaxMana() > 0)
            maxMana += String.format(" <dark_gray>( %+d from bonuses )", (int) stats.getExtraMaxMana());

        p.sendMessage(MiniMessage.miniMessage().deserialize(String.format(maxMana)));
    }

    private void displayMaxManaRegen(Player p, PlayerStats stats) {
        String maxManaRegen = String.format("<gray>|  Max Mana Regen: <dark_aqua>%.2f mana/s", stats.getTotalMaxManaRegen()*20);
        if(stats.getExtraMaxManaRegen() > 0)
            maxManaRegen += String.format(" <dark_gray>( %+.2f from bonuses )", stats.getExtraMaxManaRegen()*20);

        p.sendMessage(MiniMessage.miniMessage().deserialize(maxManaRegen));
    }
    private void displayManaRegenRate(Player p, PlayerStats stats) {
        String manaRegenRate = String.format("<gray>|  Mana Regen Rate: <dark_aqua>%.2f mana/s^2", stats.getTotalManaRegenRate()*400);
        if(stats.getExtraManaRegenRate() > 0)
            manaRegenRate += String.format(" <dark_gray>( %+.2f from bonuses )", stats.getExtraManaRegenRate()*400);

        p.sendMessage(MiniMessage.miniMessage().deserialize(manaRegenRate));
    }
    private void displayDamageMultiplier(Player p, PlayerStats stats) {
        String damageMultiplier = String.format("<gray>|  Damage Multiplier: <red>%.2fx", stats.getMagicStats().getTotalDamageMultiplier() + 1);
        if(stats.getExtraDamageMultiplier() > 0)
            damageMultiplier += String.format(" <dark_gray>( %+.2f from bonuses )", stats.getExtraDamageMultiplier());

        p.sendMessage(MiniMessage.miniMessage().deserialize(damageMultiplier));
    }
    private void displayMagicArmor(Player p, PlayerStats stats) {
        String armor = String.format("<gray>|  Magic Armor: <light_purple>%.2fx", stats.getMagicStats().getTotalMagicArmor());
        if(stats.getMagicStats().getExtraMagicArmor() > 0)
            armor += String.format(" <dark_gray>( %+.2f from bonuses )", stats.getMagicStats().getExtraMagicArmor());

        p.sendMessage(MiniMessage.miniMessage().deserialize(armor));
    }
}
