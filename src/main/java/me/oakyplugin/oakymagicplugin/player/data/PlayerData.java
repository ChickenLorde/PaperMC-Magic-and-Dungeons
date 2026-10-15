package me.oakyplugin.oakymagicplugin.player.data;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.MagicStats;
import me.oakyplugin.oakymagicplugin.items.Usable;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final UUID playerUUID;

    private boolean rightClickDown;
    private boolean canInteract;

    private double rightClickDownTime,rightClickUpTime;

    private int sneakTicks;

    private PlayerStats stats;
    private PlayerAccessories accessories;
    private PlayerSpellLoadout spellLoadout;

    private final String PLAYER_DATA_FOLDER_PATH;

    public PlayerData(Player player) {
        this.playerUUID = player.getUniqueId();
        rightClickDown = false;

        rightClickDownTime = rightClickUpTime = sneakTicks = 0;

        stats = new PlayerStats();
        accessories = new PlayerAccessories(this);
        spellLoadout = new PlayerSpellLoadout(this);
        canInteract = true;

        PLAYER_DATA_FOLDER_PATH = OakyMagicPlugin.getPluginFolder()+"/playerData/"+playerUUID;
    }
    public Player getPlayer() {return Bukkit.getPlayer(playerUUID);}

    public void tickAll() {
        Player p = Bukkit.getServer().getPlayer(playerUUID);
        if(p == null) return;
        rightClickTracking(p);
        if(p.isSneaking()) sneakTicks ++;
        else sneakTicks = 0;

        stats.tick();
    }
    public PlayerStats getStats() {return stats;}
    public PlayerAccessories getAccessories() {return accessories;}
    public PlayerSpellLoadout getSpellLoadout() {return spellLoadout;}

    public double getRightClickDownTime() { return rightClickDownTime;}
    public double getRightClickUpTime() { return rightClickUpTime;}

    private void rightClickTracking(Player p) {
        if(!p.isHandRaised()) rightClickDown = false;

        if(rightClickDown) {
            rightClickDownTime++;
            rightClickUpTime = 0;
        }
        else {
            rightClickDownTime = 0;
            rightClickUpTime++;
        }
    }

    public void rightClick() { rightClickDown = true;}
    public void resetRightClick() {rightClickDown = false; rightClickDownTime = 0;}
    public boolean isRightClicking() { return rightClickDownTime > 0;}
    public int getSneakTicks() {return sneakTicks;}

    public boolean isHoldingSpell() {
        Player p = Bukkit.getPlayer(playerUUID);
        if(p == null) return false;
        Usable usable = OakyMagicPlugin.getPlugin().getItemManager().getUsableFromItem(p.getInventory().getItemInMainHand());

        return usable instanceof Spell;
    }
    public Spell getHeldSpell() {
        Player p = Bukkit.getPlayer(playerUUID);
        if(p == null) return null;
        Usable usable = OakyMagicPlugin.getPlugin().getItemManager().getUsableFromItem(p.getInventory().getItemInMainHand());

        if(!(usable instanceof Spell spell)) return null;

        return spell;
    }

    public boolean canInteract() {return canInteract;}
    public void setCanInteract(boolean bool) { canInteract = bool; }

    //region save & load
    public void saveToFile() {
        File f = new File(PLAYER_DATA_FOLDER_PATH+"/stats.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

        File acc = new File(PLAYER_DATA_FOLDER_PATH+"/accessories.yml");
        FileConfiguration accCfg = YamlConfiguration.loadConfiguration(acc);


        cfg.set("mana",stats.getMana());
        cfg.set("maxMana",stats.getBaseMaxMana());
        cfg.set("maxManaRegen",stats.getBaseMaxManaRegen());
        cfg.set("manaRegenRate",stats.getBaseManaRegenRate());
        cfg.set("spellLoadout",spellLoadout.getSpellIds());
        cfg.createSection("magicStats");
        cfg.set("damageMultiplier",stats.getMagicStats().getBaseDamageMultiplier());
        cfg.set("magicArmor",stats.getMagicStats().getBaseMagicArmor());

        accCfg.set("accessories",accessories.getAccessoryIds());


        try{ cfg.save(f); accCfg.save(acc); } catch(IOException e) { //noinspection UnstableApiUsage
            Bukkit.getServer().getLogger().info("Could not save player data!"); }


    }

    public void loadData() {
        File f = new File(PLAYER_DATA_FOLDER_PATH+"/stats.yml");
        File acc = new File(PLAYER_DATA_FOLDER_PATH+"/accessories.yml");

        if(!f.exists()) {
            stats = new PlayerStats();
        } else {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            stats = new PlayerStats(cfg.getDouble("mana"),cfg.getDouble("maxMana"),cfg.getDouble("manaRegenRate"),cfg.getDouble("maxManaRegen"), new MagicStats());
            List<String> spellLoadoutList = cfg.getStringList("spellLoadout");
            for(String s : spellLoadoutList) {
                spellLoadout.addSpell(s);
            }
        }

        if(!acc.exists()) {
            accessories = new PlayerAccessories(this);
        } else {
            FileConfiguration accCfg = YamlConfiguration.loadConfiguration(acc);
            accessories = new PlayerAccessories(this);
            List<String> accessoriesList = accCfg.getStringList("accessories");
            for(String s : accessoriesList)
                accessories.addAccessory(s);
        }


    }
    //endregion
}
