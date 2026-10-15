package me.oakyplugin.oakymagicplugin.commands;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.EntityManager;
import me.oakyplugin.oakymagicplugin.entity.SpellDamage;
import me.oakyplugin.oakymagicplugin.items.spells.ManaBolt;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpellDamageSelf implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        EntityManager manager = OakyMagicPlugin.getPlugin().getEntityManager();

        if(!(commandSender instanceof LivingEntity e)) return true;

        double amount = Double.parseDouble(strings[0]);

        if(strings[1].isEmpty()) return true;

        Player p = Bukkit.getPlayer(strings[1]);
        if(p == null) return true;

        new SpellDamage(amount, e, p, OakyMagicPlugin.getPlugin().getItemManager().getCustomItem(ManaBolt.class), 0);
        return true;
    }
}
