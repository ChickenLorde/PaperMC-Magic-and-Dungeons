package me.oakyplugin.oakymagicplugin.commands;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.dungeons.Dungeon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TestGenerateDungeon implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player p)) return true;

        Dungeon d = new Dungeon();
        try {
            d.testCreateDungeon();
            d.addPlayer(p);
            d.teleportPlayer(p);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
