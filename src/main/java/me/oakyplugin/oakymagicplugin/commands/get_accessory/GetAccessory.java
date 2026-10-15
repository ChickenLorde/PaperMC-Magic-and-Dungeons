package me.oakyplugin.oakymagicplugin.commands.get_accessory;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.accessories.Accessory;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GetAccessory implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player p)) return false;
        String id = strings[0];
        Accessory acc = OakyMagicPlugin.getPlugin().getItemManager().getAccessory(id);

        if(acc == null) {
            commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<red> Accessory not found"));
            return true;
        }

        int amount = 1;
        if(strings.length > 1 && !strings[1].isEmpty()) {
            try {
                amount = Integer.parseInt(strings[1]);
                if (amount < 1) {
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Amount must be a positive integer"));
                    return true;
                }
            } catch (NumberFormatException e) {
                p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Amount must be a positive integer"));
                return true;
            }
        }

        ItemStack item = acc.getItem(amount);
        p.getInventory().addItem(item);
        return true;
    }
}
