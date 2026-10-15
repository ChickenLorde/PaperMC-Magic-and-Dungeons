package me.oakyplugin.oakymagicplugin.player;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private final OakyMagicPlugin plugin;
    private final Map<Player, PlayerData> playerDataMap = new HashMap<>();
    private final PlayerTick playerTick;

    public PlayerManager(OakyMagicPlugin plugin) {this.plugin = plugin;
        playerTick  = new PlayerTick(plugin);}

    public void tick() {
        for(Player p : playerDataMap.keySet()) {
            playerTick.tick(p);
            PlayerData data = playerDataMap.get(p);
            data.tickAll();

        }
    }

    public void registerPlayer(Player p) {
        if(playerDataMap.containsKey(p)) return;

        playerDataMap.put(p, new PlayerData(p));
    }

    public void unregisterPlayer(Player p) {
        if(!playerDataMap.containsKey(p)) return;

        playerDataMap.remove(p);
    }

    public void resetPlayer(Player p) {
        playerDataMap.computeIfPresent(p, (k, v) -> new PlayerData(k));
    }
    public PlayerData getPlayerData(Player p)
    {
        return playerDataMap.computeIfAbsent(p, PlayerData::new);
    }
}
