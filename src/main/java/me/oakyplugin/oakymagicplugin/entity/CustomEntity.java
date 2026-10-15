package me.oakyplugin.oakymagicplugin.entity;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.bosses.elite_necromancer.EliteNecromancer;
import me.oakyplugin.oakymagicplugin.entity.custom_entities.FunZombieWizard;
import me.oakyplugin.oakymagicplugin.entity.custom_entities.IcicleThrower;
import me.oakyplugin.oakymagicplugin.entity.custom_entities.ZombieApprentice;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public abstract class CustomEntity {

    private LivingEntity e;
    private final Class<? extends LivingEntity> type;
    private Location spawnLocation;
    private final LootPool lootPool;
    private final int lootPoolRolls;

    private int ticksAlive;

    protected final EntityManager entityManager;

    public CustomEntity(Class<? extends LivingEntity> entityClass, EntityManager manager, LootPool lootPool, int lootPoolRolls) {
        type = entityClass;

        this.lootPool = lootPool;
        this.lootPoolRolls = lootPoolRolls;

        entityManager = manager;
    }
    public CustomEntity(Class<? extends LivingEntity> entityClass, EntityManager manager) {
        type = entityClass;

        this.lootPool = null;
        this.lootPoolRolls = 0;

        entityManager = manager;
    }

    public boolean entityExists() {
        return e != null && e.isValid();
    }
    public LivingEntity getEntity() {return e;}
    public Location getSpawnLocation() {return spawnLocation;}
    public int getTicksAlive() {return ticksAlive;}

    public final void spawn(Location spawnLoc) {
        e = spawnLoc.getWorld().spawn(spawnLoc, type);
        spawnLocation = spawnLoc.clone();
        onSpawn();
        register();
    }
    private void register() {
        entityManager.registerCustomEntity(this);
    }
    protected void onSpawn() {}

    public void tryDropLoot() {
        if(lootPool == null) return;

        for(int i = 0; i < lootPoolRolls; i++) {
            ItemStack stack = lootPool.roll().getItemStack();
            getEntity().getWorld().dropItemNaturally(getEntity().getLocation(), stack);
        }
    }

    protected final void masterTick() {
        if(entityExists())
            ticksAlive++;
        tick();
    }

    protected void tick() {}

    public final void saveData() {
        LivingEntity e = getEntity();

        PersistentDataContainer pdc = e.getPersistentDataContainer();

        pdc.set(SaveDataKeys.hasData, PersistentDataType.BOOLEAN, true);
        pdc.set(SaveDataKeys.type, PersistentDataType.STRING ,getClass().getSimpleName());
        pdc.set(SaveDataKeys.uuid, PersistentDataType.STRING, e.getUniqueId().toString());
        pdc.set(SaveDataKeys.ticksAlive, PersistentDataType.INTEGER, ticksAlive);
        pdc.set(SaveDataKeys.spawnX, PersistentDataType.DOUBLE, spawnLocation.getX());
        pdc.set(SaveDataKeys.spawnY, PersistentDataType.DOUBLE, spawnLocation.getY());
        pdc.set(SaveDataKeys.spawnZ, PersistentDataType.DOUBLE, spawnLocation.getZ());
        pdc.set(SaveDataKeys.spawnYaw, PersistentDataType.FLOAT, spawnLocation.getYaw());
        pdc.set(SaveDataKeys.spawnPitch, PersistentDataType.FLOAT, spawnLocation.getPitch());
        pdc.set(SaveDataKeys.spawnWorld, PersistentDataType.STRING, spawnLocation.getWorld().getName());

        saveAdditionalData(pdc);

        Bukkit.getLogger().info("Saved "+getClass().getSimpleName());
    }

    @SuppressWarnings("DataFlowIssue")
    public static CustomEntity loadCustomEntityFromEntity(LivingEntity e) {

        PersistentDataContainer pdc = e.getPersistentDataContainer();
        if(!pdc.has(SaveDataKeys.hasData))
            return null;
        else
            Bukkit.getLogger().info(e.getName()+" is a customEntity");

        String type = pdc.get(SaveDataKeys.type, PersistentDataType.STRING);
        CustomEntity ce = switch(type) {
            case "ZombieApprentice" -> new ZombieApprentice();
            case "IcicleThrower" -> new IcicleThrower();
            case "FunZombieWizard" -> new FunZombieWizard();
            case "EliteNecromancer" -> new EliteNecromancer();
            default -> null;
        };

        double x = pdc.get(SaveDataKeys.spawnX, PersistentDataType.DOUBLE);
        double y = pdc.get(SaveDataKeys.spawnY, PersistentDataType.DOUBLE);
        double z = pdc.get(SaveDataKeys.spawnZ, PersistentDataType.DOUBLE);
        float yaw = pdc.get(SaveDataKeys.spawnYaw, PersistentDataType.FLOAT);
        float pitch = pdc.get(SaveDataKeys.spawnPitch, PersistentDataType.FLOAT);
        String world = pdc.get(SaveDataKeys.spawnWorld, PersistentDataType.STRING);

        ce.spawnLocation = new Location(Bukkit.getWorld(world),x,y,z,yaw,pitch);
        ce.e = e;
        ce.ticksAlive = pdc.get(SaveDataKeys.ticksAlive, PersistentDataType.INTEGER);
        ce.loadAdditionalData(pdc);
        ce.register();

        return ce;
    }

    public Component getData() {
        Component data = Component.text(String.format("CustomEntity Type: %s\nEntityType: %s\nUUID: %s\nSpawn Location: %s\nTicks Alive: %s\n",
                getClass().getSimpleName(), getEntity().getType().name(), getEntity().getUniqueId(), spawnLocation.toString(), ticksAlive));
        data = getAdditionalData(data);
        return data;
    }

    protected Component getAdditionalData(Component data) { return data; }

    protected void loadAdditionalData(PersistentDataContainer pdc) {}
    protected void saveAdditionalData(PersistentDataContainer pdc) {}


    private static final class SaveDataKeys {

        private static final NamespacedKey hasData = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "has_custom_entity_data");

        private static final NamespacedKey type = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "custom_entity_type");
        private static final NamespacedKey uuid = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "uuid");
        private static final NamespacedKey ticksAlive = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "ticks_alive");
        private static final NamespacedKey spawnX = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "spawn_x");
        private static final NamespacedKey spawnY = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "spawn_y");
        private static final NamespacedKey spawnZ = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "spawn_z");
        private static final NamespacedKey spawnYaw = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "spawn_yaw");
        private static final NamespacedKey spawnPitch = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "spawn_pitch");
        private static final NamespacedKey spawnWorld = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "spawn_world");







    }
}
