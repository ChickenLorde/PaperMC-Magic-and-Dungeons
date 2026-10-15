package me.oakyplugin.oakymagicplugin.commands.get_usable;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.Usable;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GetSpell implements CommandExecutor {

    private final OakyMagicPlugin plugin = OakyMagicPlugin.getPlugin();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player p)) return false;
        String spellId = strings[0];
        Usable usable = plugin.getItemManager().getUsable(spellId);

        if(usable == null) {
            commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<red> Usable not found"));
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

        ItemStack item = usable.getItem(amount);
        p.getInventory().addItem(item);
        return true;
    }
}
