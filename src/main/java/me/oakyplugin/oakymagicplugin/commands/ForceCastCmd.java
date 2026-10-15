package me.oakyplugin.oakymagicplugin.commands;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ForceCastCmd implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof LivingEntity e)) {
            commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<red>commandSender must be of type LivingEntity"));
            return true;
        }
        String spellName = strings.length == 0 || strings[0].isEmpty() ? "" : strings[0];

        if(spellName.isEmpty()) {
            commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<red>First argument is missing a string"));
            return true;
        }

        CustomItem customItem = OakyMagicPlugin.getPlugin().getItemManager().getCustomItemByName(spellName);

        if(!(customItem instanceof Spell spell)) {
            commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Spell not found"));
            return true;
        }

        spell.cast(e, e.getEyeLocation());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            List<String> types = new ArrayList<>();
            CustomItem[] items = OakyMagicPlugin.getPlugin().getItemManager().getCustomItems();
            for(CustomItem item : items) {
                if(!(item instanceof Spell spell)) continue;
                types.add(spell.getId());
            }
            return filter(types, strings[0]);
        }

        return completions;
    }

    private List<String> filter(List<String> options, String currentInput) {
        List<String> filtered = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(currentInput.toLowerCase())) {
                filtered.add(option);
            }
        }
        return filtered;
    }
}
