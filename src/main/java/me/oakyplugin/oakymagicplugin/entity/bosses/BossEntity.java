package me.oakyplugin.oakymagicplugin.entity.bosses;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.LootPool;
import me.oakyplugin.oakymagicplugin.entity.MagicEntity;
import me.oakyplugin.oakymagicplugin.entity.MagicStats;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class BossEntity extends MagicEntity {

    protected String bossName;

    protected final ArrayList<BossAttack> attacks = new ArrayList<>();
    protected BossAttack activeAttack = null;

    private final String saveString = "bossData.";

    private TextDisplay hpDisplay;
    private TextDisplay bossNameDisplay;

    protected int attackCooldown;

    public BossEntity(Class<? extends LivingEntity> entityClass, MagicStats stats, LootPool lootPool, int lootPoolRolls) {
        super(entityClass, stats, lootPool, lootPoolRolls);
        bossName = "BossEntity";
    }

    public BossEntity(Class<? extends LivingEntity> entityClass, MagicStats stats) {
        super(entityClass, stats);
        bossName = "BossEntity";
    }

    @Override
    protected void onSpawn() {
        onBossSpawn();
        spawnHPDisplay();

    }

    protected void onBossSpawn() {}

    private void spawnHPDisplay() {
        Location loc = getEntity().getLocation();
        hpDisplay = loc.getWorld().spawn(loc, TextDisplay.class);
        Transformation t = hpDisplay.getTransformation();
        t.getTranslation().set(0,0.75,0);
        hpDisplay.setTransformation(t);
        hpDisplay.setBillboard(Display.Billboard.CENTER);
        entityManager.registerAttachment(hpDisplay, getEntity());
    }
    private void spawnBossName() {
        Location loc = getEntity().getLocation();
        bossNameDisplay = loc.getWorld().spawn(loc, TextDisplay.class);
        Transformation t = hpDisplay.getTransformation();
        t.getTranslation().set(0,0.25,0);
        bossNameDisplay.setTransformation(t);
        bossNameDisplay.setBillboard(Display.Billboard.CENTER);
        entityManager.registerAttachment(bossNameDisplay, getEntity());
        bossNameDisplay.text(MiniMessage.miniMessage().deserialize(bossName));
    }

    @Override
    protected final void tick() {
        if(hpDisplay == null || !hpDisplay.isValid())
            spawnHPDisplay();
        if(bossNameDisplay == null || !bossNameDisplay.isValid())
            spawnBossName();
        hpDisplay.text(MiniMessage.miniMessage().deserialize("<red>"+(int)Math.ceil(getEntity().getHealth())+" <white>HP"));

        getEntity().addPassenger(hpDisplay);
        hpDisplay.eject();
        getEntity().addPassenger(bossNameDisplay);
        bossNameDisplay.eject();

        for(BossAttack a : attacks) a.reduceCooldown();
        if(attackCooldown > 0) attackCooldown--;

        bossTick();
    }

    protected void bossTick() {}


    @Override
    protected void saveMagicEntityData(PersistentDataContainer pdc) {
        pdc.set(SaveDataKeys.hpDisplayUUID, PersistentDataType.STRING, hpDisplay.getUniqueId().toString());
        pdc.set(SaveDataKeys.bossNameDisplayUUID, PersistentDataType.STRING, bossNameDisplay.getUniqueId().toString());
    }

    @Override
    protected void loadMagicEntityData(PersistentDataContainer pdc) {
        String hp = pdc.get(SaveDataKeys.hpDisplayUUID, PersistentDataType.STRING);
        if(hp != null && !hp.isEmpty()) {
            UUID hpDisplayUUID = UUID.fromString(hp);
            Entity hpDisplayEntity = Bukkit.getEntity(hpDisplayUUID);
            if (hpDisplayEntity instanceof TextDisplay t) hpDisplay = t;
            entityManager.registerAttachment(hpDisplay, getEntity());
        }

        String name = pdc.get(SaveDataKeys.bossNameDisplayUUID, PersistentDataType.STRING);
        if(name != null && !name.isEmpty()) {
            UUID nameDisplayUUID = UUID.fromString(name);
            Entity nameDisplayEntity = Bukkit.getEntity(nameDisplayUUID);
            if (nameDisplayEntity instanceof TextDisplay t) bossNameDisplay = t;
            entityManager.registerAttachment(bossNameDisplay, getEntity());
        }
    }

    private static final class SaveDataKeys {
        private final static NamespacedKey hpDisplayUUID = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "boss_entity.hp_display_uuid");
        private final static NamespacedKey bossNameDisplayUUID = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "boss_entity.boss_name_display_uuid");

    }
}
