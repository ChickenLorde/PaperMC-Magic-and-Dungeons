package me.oakyplugin.oakymagicplugin.commands.get_customitem;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GetCustomItemTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> types = new ArrayList<>();
            CustomItem[] items = OakyMagicPlugin.getPlugin().getItemManager().getCustomItems();
            for(CustomItem item : items) {
                types.add(item.getId());
            }
            return filter(types, args[0]);
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

