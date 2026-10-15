package me.oakyplugin.oakymagicplugin.entity;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public abstract class MagicEntity extends CustomEntity {

    private MagicStats magicStats;

    private final String saveString = "magicStats.";

    public MagicEntity(Class<? extends LivingEntity> entityClass, MagicStats stats, LootPool lootPool, int lootPoolRolls) {
        super(entityClass, OakyMagicPlugin.getPlugin().getEntityManager(), lootPool, lootPoolRolls);
        this.magicStats = stats;
    }

    public MagicEntity(Class<? extends LivingEntity> entityClass, MagicStats stats) {
        super(entityClass, OakyMagicPlugin.getPlugin().getEntityManager());
        this.magicStats = stats;
    }

    public MagicStats getMagicStats() {
        return magicStats;
    }

    @Override
    protected final void saveAdditionalData(PersistentDataContainer pdc) {
        pdc.set(SaveDataKeys.baseDamageMultiplier, PersistentDataType.DOUBLE, magicStats.getBaseDamageMultiplier());
        pdc.set(SaveDataKeys.baseMagicArmor, PersistentDataType.DOUBLE, magicStats.getBaseMagicArmor());

        saveMagicEntityData(pdc);
    }

    @Override
    protected final void loadAdditionalData(PersistentDataContainer pdc) {
        double dmg = pdc.get(SaveDataKeys.baseDamageMultiplier, PersistentDataType.DOUBLE);
        double armor = pdc.get(SaveDataKeys.baseMagicArmor, PersistentDataType.DOUBLE);

        magicStats = MagicStats.builder()
                .damageMultiplier(dmg)
                .magicArmor(armor)
                .build();
        loadMagicEntityData(pdc);
    }

    @Override
    protected Component getAdditionalData(Component data) {
        data = data.append(Component.text(String.format("MagicStats: \n%s",magicStats.toString())));
        return getAdditionalMagicEntityData(data);
    }

    protected Component getAdditionalMagicEntityData(Component data){ return data;}

    protected void loadMagicEntityData(PersistentDataContainer pdc) {}
    protected void saveMagicEntityData(PersistentDataContainer pdc) {}

    private final static class SaveDataKeys {

        private final static NamespacedKey baseDamageMultiplier = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "magic_stats.base_damage_multiplier");
        private final static NamespacedKey baseMagicArmor = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "magic_stats.base_magic_armor");

    }
}
