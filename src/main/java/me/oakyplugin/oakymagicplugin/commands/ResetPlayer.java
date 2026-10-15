package me.oakyplugin.oakymagicplugin.commands;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ResetPlayer implements CommandExecutor {

    private final OakyMagicPlugin plugin = JavaPlugin.getPlugin(OakyMagicPlugin.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player p)) return false;

        String pName;

        if(strings.length > 0) {
            pName = strings[0];
        }
        else pName = p.getName();
        Player target = Bukkit.getPlayer(pName);
        if(target == null)
        {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Player not found"));
            return true;
        }
        plugin.getPlayerManager().resetPlayer(target);
        return true;
    }
}
