package me.oakyplugin.oakymagicplugin.commands.get_accessory;
import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import me.oakyplugin.oakymagicplugin.items.accessories.Accessory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetAccessoryTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> types = new ArrayList<>();
            CustomItem[] items = OakyMagicPlugin.getPlugin().getItemManager().getCustomItems();
            for(CustomItem item : items) {
                if(!(item instanceof Accessory acc)) continue;
                types.add(acc.getId());
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
