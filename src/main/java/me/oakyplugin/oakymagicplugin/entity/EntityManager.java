package me.oakyplugin.oakymagicplugin.entity;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.bosses.BossAttack;
import me.oakyplugin.oakymagicplugin.utility.BiMap;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.Name;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class EntityManager {

    private final Set<CustomEntity> customEntities = new HashSet<>();
    private final Set<MagicEntity> magicEntities = new HashSet<>();
    private final Set<BossAttack> bossAttacks = new HashSet<>();

    public static final NamespacedKey entityAttachmentKey = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "entity_attachment");

    private final HashMap<LivingEntity, MagicStats> entityMagicStats = new HashMap<>();

    private final BiMap<Player, PuppetSpellEntity> playerPuppets = new BiMap<>();

    private final HashMap<LivingEntity, SpellDamage> fatalDamageMap = new HashMap<>();

    private final HashMap<Entity, LivingEntity> entityAttachments = new HashMap<>();

    private int currentTick;

    void damageEntity(SpellDamage damage) {
        LivingEntity target = damage.getTarget();
        LivingEntity source = damage.getSource();

        if(target.isInvulnerable() || (target instanceof Player p && p.getGameMode().isInvulnerable())) return;

        double dmg = damage.getDamage();
        MagicStats targetMagicStats = null;
        MagicStats sourceMagicStats = null;

        if(target instanceof Player p)
            targetMagicStats = OakyMagicPlugin.getPlugin().getPlayerManager().getPlayerData(p).getStats().getMagicStats();
        else if(entityMagicStats.containsKey(target))
            targetMagicStats = entityMagicStats.get(target);

        if(source instanceof Player p)
            sourceMagicStats = OakyMagicPlugin.getPlugin().getPlayerManager().getPlayerData(p).getStats().getMagicStats();
        else if(entityMagicStats.containsKey(source))
            sourceMagicStats = entityMagicStats.get(source);

        double finalDamage;

        finalDamage = dmg;
        if(sourceMagicStats != null) {
            finalDamage += finalDamage * sourceMagicStats.getTotalDamageMultiplier();
        }
        if(targetMagicStats != null) {
            double armor = targetMagicStats.getTotalMagicArmor();
            finalDamage = finalDamage - (finalDamage * (armor / (armor + 6 * Math.sqrt(finalDamage))));
        }

        double amount = Math.max(target.getHealth() - finalDamage, 0);

        if(amount == 0) {
            if (target instanceof Player p && isPlayerPuppet(p)) {
                getPuppetFromPlayer(p).returnToOwner();
                return;
            } else
                fatalDamageMap.put(target, damage);
        } else
            fatalDamageMap.remove(target);

        target.setNoDamageTicks(0);

        DamageType dType;
        if(target instanceof EnderDragon)
            dType = DamageType.EXPLOSION;
        else
            dType = DamageType.INDIRECT_MAGIC;

        target.damage(0.001, DamageSource.builder(dType)
                .withDirectEntity(damage.getSource())
                .withCausingEntity(damage.getSource())
                .withDamageLocation(damage.getSource().getLocation())
                .build());

        if(target instanceof Player p && isPlayerPuppet(p)) {
            getPuppetFromPlayer(p).damagePuppet(finalDamage);
        }

        target.setHealth(amount);
    }

    void registerCustomEntity(CustomEntity e) {
        customEntities.add(e);
        if(e instanceof MagicEntity me) {
            magicEntities.add(me);
            entityMagicStats.put(me.getEntity(), me.getMagicStats());
        }
    }
    void registerPuppet(Player p, PuppetSpellEntity e) {
        playerPuppets.add(p, e);
    }

    public void registerAttachment(Entity attachment, LivingEntity owner) {
        entityAttachments.put(attachment, owner);
        attachment.getPersistentDataContainer().set(entityAttachmentKey, PersistentDataType.STRING, owner.getUniqueId().toString());
    }

    public void tick() {
        currentTick++;
        updateEntities();
    }

    public void updateEntities() {
        for(CustomEntity e : new HashSet<>(customEntities)) {
            if(!e.entityExists()) {
                customEntities.remove(e);
                File file = new File(getCustomEntityFolderPath(e));
                if(file.exists())
                    Utility.deleteRecursively(file);

                if (e instanceof PuppetSpellEntity pse) {
                    pse.getCurrentChunk().setForceLoaded(false);
                    pse.returnToOwner();
                    playerPuppets.removeRight(pse);
                }
            } else {
                if (e instanceof PuppetSpellEntity pse) {
                    pse.getOwner().setHealth(pse.getPuppetHP());

                    if(currentTick % 100 == 0)
                        pse.updateChunk();
                }
            }
            e.masterTick();
        }
        for(Entity e : entityAttachments.keySet()) {
            LivingEntity le = entityAttachments.get(e);
            if(le == null || !le.isValid()) e.remove();
        }
        entityAttachments.keySet().removeIf(k -> k == null || !k.isValid());
        entityAttachments.keySet().removeIf(k -> entityAttachments.get(k) == null || !entityAttachments.get(k).isValid());
    }


    public Set<CustomEntity> getCustomEntities() { return customEntities;}

    public CustomEntity getCustomEntityFromEntity(LivingEntity e) {
        for(CustomEntity ce : customEntities) {
            if(ce.getEntity().equals(e))
                return ce;
        }
        return null;
    }

    public boolean isPlayerPuppet(Player p) {
        return playerPuppets.containsLeft(p);
    }

    public PuppetSpellEntity getPuppetFromPlayer(Player p) {
        return playerPuppets.getFromLeft(p);
    }
    public PuppetSpellEntity getPuppetFromMannequin(Mannequin e) {
        for(PuppetSpellEntity m : playerPuppets.getAllRight())
            if(m.getEntity().equals(e)) return m;
        return null;
    }

    public String getCustomEntityFolderPath(LivingEntity e) {
        if(getCustomEntityFromEntity(e) == null) {
            return "";
        }
        return OakyMagicPlugin.getPluginFolder() + "/customEntities/"+e.getUniqueId();
    }
    public String getCustomEntityFolderPath(CustomEntity e) {
        return OakyMagicPlugin.getPluginFolder() + "/customEntities/"+e.getEntity().getUniqueId();
    }

    public HashMap<LivingEntity, SpellDamage> getFatalDamageMap() {return fatalDamageMap;}

    public void saveCustomEntities() {
        for(CustomEntity e : customEntities) {
            e.saveData();
        }
    }

    public void trySaveCustomEntity(LivingEntity e) {
        CustomEntity ce = getCustomEntityFromEntity(e);
        if(ce == null) return;

        ce.saveData();
        Bukkit.getLogger().info("Saved "+ce.getClass().getSimpleName());
    }
    public void tryLoadCustomEntity(LivingEntity e) {
        CustomEntity ce = CustomEntity.loadCustomEntityFromEntity(e);
        if(ce == null) {
            //Bukkit.getLogger().warning("Failed to load CustomEntity from "+e.getName());
        } else {
            registerCustomEntity(ce);
            Bukkit.getLogger().info("Loaded "+ce.getClass().getSimpleName());
        }
    }
}
