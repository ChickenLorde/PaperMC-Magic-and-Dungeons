package me.oakyplugin.oakymagicplugin.commands;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.dungeons.rooms.CardinalDirection;
import me.oakyplugin.oakymagicplugin.dungeons.rooms.DungeonRoom;
import me.oakyplugin.oakymagicplugin.dungeons.rooms.DungeonRoomTemplateRegistry;
import me.oakyplugin.oakymagicplugin.dungeons.rooms.DungeonRoomType;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;

public class TestGenerateHallway implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player p)) return true;

        try {
            DungeonRoomTemplateRegistry reg = OakyMagicPlugin.getPlugin().getDungeonRoomTemplateRegistry();
            DungeonRoom room = new DungeonRoom(null, reg.getTemplate("corridor:ruins_straight_0"), Utility.entityFacingToCardinal(p), p.getLocation(),0);

            room.placeRoom();
            room.getRoomBounds();

            new BukkitRunnable() {
                @Override
                public void run() {
                    room.deleteRoomFromWorld();
                }
            }.runTaskLater(OakyMagicPlugin.getPlugin(), 150);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
