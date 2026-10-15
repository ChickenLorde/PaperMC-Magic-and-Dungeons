package me.oakyplugin.oakymagicplugin.commands;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.dungeons.rooms.DungeonRoom;
import me.oakyplugin.oakymagicplugin.dungeons.rooms.DungeonRoomTemplate;
import me.oakyplugin.oakymagicplugin.dungeons.rooms.DungeonRoomTemplateRegistry;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TestGenerateRoom implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player p)) return true;

        String id = "";

        if(strings.length < 1) return true;

        id = strings[0];

        DungeonRoomTemplateRegistry reg = OakyMagicPlugin.getPlugin().getDungeonRoomTemplateRegistry();

        DungeonRoomTemplate template = reg.getTemplate(id);

        if(template == null) {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(String.format("<red>Room with id %s not found, list of available ids:",id)));
            for(String x : reg.getTemplateIds()) {
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(String.format("<red>%s",x)));
            }

        } else {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(String.format("<green>Room with id %s was found",id)));

            try {
                DungeonRoom room = new DungeonRoom(null, template, Utility.entityFacingToCardinal(p),p.getLocation(), 0);
                room.placeRoom();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }
}
