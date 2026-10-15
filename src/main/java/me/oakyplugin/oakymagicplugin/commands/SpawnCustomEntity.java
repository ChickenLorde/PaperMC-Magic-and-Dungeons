package me.oakyplugin.oakymagicplugin.commands;

import me.oakyplugin.oakymagicplugin.entity.bosses.elite_necromancer.EliteNecromancer;
import me.oakyplugin.oakymagicplugin.entity.custom_entities.FunZombieWizard;
import me.oakyplugin.oakymagicplugin.entity.custom_entities.IcicleThrower;
import me.oakyplugin.oakymagicplugin.entity.custom_entities.ZombieApprentice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpawnCustomEntity implements CommandExecutor, TabCompleter {

    private final String zombieApprentice = "zombie_apprentice";
    private final String funZombieWizard = "fun_zombie_wizard";
    private final String icicleCaster = "icicle_caster";
    private final String eliteNecromancer = "elite_necromancer";

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player p)) {
            return true;
        }

        if(strings.length == 0) {
            return true;
        }

        String entityName = strings[0];



        switch(entityName) {
            case zombieApprentice: new ZombieApprentice().spawn(p.getLocation()); break;
            case funZombieWizard: new FunZombieWizard().spawn(p.getLocation()); break;
            case icicleCaster: new IcicleThrower().spawn(p.getLocation()); break;
            case eliteNecromancer: new EliteNecromancer().spawn(p.getLocation()); break;
            default: return true;
        };
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of(zombieApprentice,funZombieWizard, icicleCaster, eliteNecromancer);
    }
}
