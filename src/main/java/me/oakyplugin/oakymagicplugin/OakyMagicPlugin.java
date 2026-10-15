package me.oakyplugin.oakymagicplugin;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.oakyplugin.oakymagicplugin.commands.*;
import me.oakyplugin.oakymagicplugin.commands.get_accessory.GetAccessory;
import me.oakyplugin.oakymagicplugin.commands.get_accessory.GetAccessoryTabCompleter;
import me.oakyplugin.oakymagicplugin.commands.get_customitem.GetCustomItem;
import me.oakyplugin.oakymagicplugin.commands.get_customitem.GetCustomItemTabCompleter;
import me.oakyplugin.oakymagicplugin.commands.get_usable.GetSpell;
import me.oakyplugin.oakymagicplugin.commands.get_usable.GetSpellTabCompleter;
import me.oakyplugin.oakymagicplugin.dungeons.DungeonManager;
import me.oakyplugin.oakymagicplugin.dungeons.rooms.DungeonRoomTemplateRegistry;
import me.oakyplugin.oakymagicplugin.entity.CustomEntity;
import me.oakyplugin.oakymagicplugin.entity.EntityManager;
import me.oakyplugin.oakymagicplugin.entity.PuppetSpellEntity;
import me.oakyplugin.oakymagicplugin.entity.SpellDamage;
import me.oakyplugin.oakymagicplugin.entity.custom_entities.IcicleThrower;
import me.oakyplugin.oakymagicplugin.entity.custom_entities.ZombieApprentice;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import me.oakyplugin.oakymagicplugin.items.ItemManager;
import me.oakyplugin.oakymagicplugin.items.Usable;
import me.oakyplugin.oakymagicplugin.items.accessories.Accessory;
import me.oakyplugin.oakymagicplugin.items.consumables.ConsumableItem;
import me.oakyplugin.oakymagicplugin.items.spells.Spell;
import me.oakyplugin.oakymagicplugin.items.spells.magic_circles.MagicCircleManager;
import me.oakyplugin.oakymagicplugin.items.spells.shield.ShieldManager;
import me.oakyplugin.oakymagicplugin.player.PlayerManager;
import me.oakyplugin.oakymagicplugin.player.data.PlayerData;
import me.oakyplugin.oakymagicplugin.player.data.PlayerSpellLoadout;
import me.oakyplugin.oakymagicplugin.projectiles.ProjectileManager;
import me.oakyplugin.oakymagicplugin.random_events.RandomEventsManager;
import me.oakyplugin.oakymagicplugin.random_events.event_entities.FallingStarEntity;
import me.oakyplugin.oakymagicplugin.status_effect.StatusEffectManager;
import me.oakyplugin.oakymagicplugin.utility.ActivateOnKill;
import me.oakyplugin.oakymagicplugin.utility.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class OakyMagicPlugin extends JavaPlugin implements Listener {

    public static final String NAMESPACE = "oakymagicplugin";
    public static final NamespacedKey ITEM_ID = new NamespacedKey(NAMESPACE, "item_id");
    public static final NamespacedKey REMOVE_ON_DISABLE = new NamespacedKey(NAMESPACE, "remove_on_disable");

    private World dungeonWorld;

    private final PlayerManager playerManager = new PlayerManager(this);
    private final ItemManager itemManager = new ItemManager();
    private final ProjectileManager projectileManager = new ProjectileManager();
    private final StatusEffectManager statusEffectManager = new StatusEffectManager();
    private final RandomEventsManager randomEventsManager = new RandomEventsManager();
    private final MagicCircleManager magicCircleManager = new MagicCircleManager();
    private final ShieldManager shieldManager = new ShieldManager();
    private final EntityManager entityManager = new EntityManager();
    private final DungeonManager dungeonManager = new DungeonManager();
    private final DungeonRoomTemplateRegistry dungeonRoomTemplateRegistry = new DungeonRoomTemplateRegistry();

    public static OakyMagicPlugin getPlugin() { return JavaPlugin.getPlugin(OakyMagicPlugin.class);}
    @Override
    public void onEnable() {

        try {
            dungeonRoomTemplateRegistry.loadTemplates();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dungeonWorld = new WorldCreator("dungeon_world").type(WorldType.FLAT).generatorSettings("{\"layers\": [{\"block\": \"barrier\", \"height\": 1}], \"biome\":\"the_void\"}").generateStructures(false).createWorld();
        if(dungeonWorld != null) {
            dungeonWorld.setSpawnLocation(0,-63,0);
            dungeonWorld.setTime(18000);
            dungeonWorld.setGameRule(GameRules.ADVANCE_TIME, false);
            dungeonWorld.setGameRule(GameRules.MOB_GRIEFING, false);
            dungeonWorld.setGameRule(GameRules.SPAWN_MOBS, false);
            dungeonWorld.setGameRule(GameRules.KEEP_INVENTORY, true);
        }

        getServer().getPluginManager().registerEvents(this, this);

        itemManager.registerAllItems();
        randomEventsManager.registerRandomEvents();

        this.getCommand("resetplayer").setExecutor(new ResetPlayer());
        this.getCommand("getspell").setExecutor(new GetSpell());
        this.getCommand("getspell").setTabCompleter(new GetSpellTabCompleter());
        this.getCommand("stats").setExecutor(new GetStats());
        this.getCommand("accessories").setExecutor(new ViewAccessories());
        this.getCommand("getaccessory").setExecutor(new GetAccessory());
        this.getCommand("getaccessory").setTabCompleter(new GetAccessoryTabCompleter());
        this.getCommand("getcustomitem").setExecutor(new GetCustomItem());
        this.getCommand("getcustomitem").setTabCompleter(new GetCustomItemTabCompleter());
        this.getCommand("spelldamageself").setExecutor(new SpellDamageSelf());
        this.getCommand("forcecast").setExecutor(new ForceCastCmd());
        this.getCommand("forcecast").setTabCompleter(new ForceCastCmd());
        this.getCommand("spawncustom").setExecutor(new SpawnCustomEntity());
        this.getCommand("spawncustom").setTabCompleter(new SpawnCustomEntity());
        this.getCommand("genhallway").setExecutor(new TestGenerateHallway());
        this.getCommand("gendungeon").setExecutor(new TestGenerateDungeon());
        this.getCommand("getroom").setExecutor(new TestGenerateRoom());

        for(World w : Bukkit.getWorlds()) {
            for(Entity e : w.getEntities()) {
                if(!e.isValid()) continue;
                PersistentDataContainer pdc = e.getPersistentDataContainer();
                if(!pdc.has(EntityManager.entityAttachmentKey)) continue;
                String s = pdc.get(EntityManager.entityAttachmentKey, PersistentDataType.STRING);
                if(s == null || s.isEmpty()) continue;
                UUID attachedUUID = UUID.fromString(s);
                Entity attached = Bukkit.getEntity(attachedUUID);
                if(!(attached instanceof LivingEntity le)) continue;
                entityManager.registerAttachment(e, le);
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                playerManager.tick();
                itemManager.tick();
                shieldManager.tick();
                projectileManager.tick();
                statusEffectManager.tick();
                randomEventsManager.tick();
                magicCircleManager.tick();
                entityManager.tick();
                dungeonManager.tick();
            }


        }.runTaskTimer(this, 0, 1L);


    }

    public PlayerManager getPlayerManager() { return playerManager; }

    public ItemManager getItemManager() {return itemManager;}
    public ProjectileManager getProjectileManager() { return projectileManager;}
    public StatusEffectManager getStatusEffectManager() { return statusEffectManager;}
    public RandomEventsManager getRandomEventsManager() {return randomEventsManager;}
    public MagicCircleManager getMagicCircleManager() {return magicCircleManager;}
    public ShieldManager getShieldManager() {return shieldManager;}
    public EntityManager getEntityManager() {return entityManager;}

    public DungeonManager getDungeonManager() {return dungeonManager;}
    public DungeonRoomTemplateRegistry getDungeonRoomTemplateRegistry() {return dungeonRoomTemplateRegistry;}

    @Override
    public void onDisable() {
        for(World world : Bukkit.getWorlds())
            for(Entity e : world.getEntities()) {
                e.setVisualFire(TriState.NOT_SET);
                if(e.getScoreboardTags().contains("removeAfterDisable"))
                    e.remove();
                if(e.getPersistentDataContainer().has(SpecialEntityKeys.removeOnDisable))
                    e.remove();
            }

        Collection<? extends Player> players = getServer().getOnlinePlayers();
        for(Player p : players) {

            if(entityManager.isPlayerPuppet(p)) {
                entityManager.getPuppetFromPlayer(p).returnToOwner();
            }
            playerManager.getPlayerData(p).saveToFile();
        }

        entityManager.saveCustomEntities();

        dungeonManager.removeAllPlayers();

        World world = Bukkit.getWorld("dungeon_world");
        if(world != null) {
            File f = world.getWorldFolder();
            if(f.exists()) {
                Utility.deleteRecursively(f);
            }
        }
    }

    public static String getPluginFolder() {
        return Bukkit.getPluginsFolder().getAbsolutePath() + "/"+NAMESPACE;
    }



    //region events
    @EventHandler
    public void playerPostRespawn(PlayerPostRespawnEvent event) {
        Player p = event.getPlayer();
        if(dungeonManager.isPlayerInDungeon(p))
            dungeonManager.removePlayerFromDungeons(p);
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if(block.getWorld().getName().equals("dungeon_world")) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        Player p = event.getPlayer();

        if(event.getTo().getWorld().getName().equals("dungeon_world")) {
            if(!dungeonManager.isPlayerInDungeon(p)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        playerManager.getPlayerData(p).loadData();
        playerManager.registerPlayer(p);
    }
    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        if(entityManager.isPlayerPuppet(p)) {
            p.teleport(entityManager.getPuppetFromPlayer(p).getEntity());
            entityManager.getPuppetFromPlayer(p).returnToOwner();
        }

        if(dungeonManager.isPlayerInDungeon(p))
            dungeonManager.removePlayerFromDungeons(p);

        playerManager.getPlayerData(p).saveToFile();
        playerManager.unregisterPlayer(p);

    }

    @EventHandler
    public void entityRegainHealth(EntityRegainHealthEvent event) {
        Entity e = event.getEntity();
        if(e instanceof Player p) {
            if(entityManager.isPlayerPuppet(p)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        HumanEntity he = event.getEntity();
        if(he instanceof Player p) {
            if(entityManager.isPlayerPuppet(p)) {
                event.setCancelled(true);
                return;
            }
        }
    }


    @EventHandler
    public void playerInteract(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        PlayerData data = playerManager.getPlayerData(p);

        if(!data.canInteract()) {
            event.setCancelled(true);
            return;
        }

        ItemStack handItem = p.getInventory().getItemInMainHand();

        if(Usable.isUsable(handItem)) data.rightClick();
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        if(entityManager.isPlayerPuppet(p)) {
            event.setCancelled(true);
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Puppets cannot drop items"));
            return;
        }

    }
    @EventHandler
    public void entityPickupItemEvent(EntityPickupItemEvent event) {
        LivingEntity e = event.getEntity();

        if(e instanceof Player p) {
            if(entityManager.isPlayerPuppet(p)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void entitiesLoad(EntitiesLoadEvent event) {
        List<Entity> entities = event.getEntities();

        for(Entity e : entities) {
            if(!(e instanceof LivingEntity le)) continue;
            entityManager.tryLoadCustomEntity(le);
        }
    }

    @EventHandler
    public void entitiesUnload(EntitiesUnloadEvent event) {
        List<Entity> entities = event.getEntities();

        for(Entity e : entities) {
            if(!(e instanceof LivingEntity le)) continue;
            entityManager.trySaveCustomEntity(le);
        }
    }

    @EventHandler
    public void playerInteractEntity(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();

        if(event.getRightClicked() instanceof Interaction e) {
             FallingStarEntity star = randomEventsManager.getStarInteractionPairs().getFromRight(e);
             if(star != null) star.onInteract(p);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        HumanEntity he = event.getWhoClicked();
        InventoryView view = event.getView();

        Component title = view.title();

        if(he instanceof Player p && entityManager.isPlayerPuppet(p)) {
            if(event.getClickedInventory() != null && !event.getClickedInventory().equals(p.getInventory())) {
                p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Puppets cannot do this"));
                event.setCancelled(true);
                return;
            }
            if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Puppets cannot do this"));
                event.setCancelled(true);
                return;
            }
        }

        if(title.equals(Component.text("Magic Accessories"))) magicAccessoriesClicked(event);
        if(title.equals(Component.text("Spell Loadout"))) spellLoadoutClicked(event);

    }
    private void spellLoadoutClicked(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player p)) {
            event.setCancelled(true);
            return;
        }
        if(event.getClick().isShiftClick()) {
            event.setCancelled(true);
            return;
        }
        if(!event.getView().getTopInventory().equals(event.getClickedInventory())) return;

        PlayerSpellLoadout spellLoadout = playerManager.getPlayerData(p).getSpellLoadout();

        ItemStack item = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if (cursor.getType().isAir()) {
            if (!itemManager.isCustomItemOfType(item, Spell.class)) {
                event.setCancelled(true);
                //p.sendMessage(Component.text("Cursor is air and item is not a spell"));
            }
            //Player is taking an accessory out
            else {
                Spell spell = itemManager.getCustomItemFromItem(item, Spell.class);
                spellLoadout.removeSpell(spell.getId());
                //p.sendMessage(Component.text("Cursor is air and item is a spell"));
            }
        } else {
            if ((item != null && !item.getType().isAir()) || !itemManager.isCustomItemOfType(cursor, Spell.class)) event.setCancelled(true);
                //Player is putting an accessory in
            else {
                Spell spell = itemManager.getCustomItemFromItem(cursor, Spell.class);
                if(!spellLoadout.addSpell(spell.getId())) {
                    event.setCancelled(true);
                    //p.sendMessage(Component.text("Spell already exists"));
                    return;
                }
                //p.sendMessage(Component.text("Cursor is not air and cursor is a spell"));
            }
        }
    }

    private void magicAccessoriesClicked(InventoryClickEvent event) {
            event.setCancelled(true);
            if(!(event.getWhoClicked() instanceof Player p)) {
                event.setCancelled(true);
                return;
            }
            if(event.getClick().isShiftClick()) {
                event.setCancelled(true);
                return;
            }

            if(event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;

            //Remove accessory
            if(event.getView().getTopInventory().equals(event.getClickedInventory())) {
                ItemStack item = event.getCurrentItem();
                Accessory acc = itemManager.getCustomItemFromItem(item, Accessory.class);

                if(acc == null) return;
                playerManager.getPlayerData(p).getAccessories().removeAccessory(acc.getId());
                event.getClickedInventory().setItem(event.getSlot(), ItemStack.empty());
                p.getInventory().addItem(item);
            }
            //Add accessory
            else {
                ItemStack item = event.getCurrentItem();
                Accessory acc = itemManager.getCustomItemFromItem(item, Accessory.class);

                if(acc == null) return;
                if(!playerManager.getPlayerData(p).getAccessories().addAccessory(acc.getId())) return;
                event.getClickedInventory().setItem(event.getSlot(), ItemStack.empty());
                event.getView().getTopInventory().addItem(item);
            }
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        LivingEntity e = event.getEntity();

        if(e.getPersistentDataContainer().has(SpecialEntityKeys.noExpDrops))
            event.setDroppedExp(0);
        if(e.getPersistentDataContainer().has(SpecialEntityKeys.noItemDrops))
            event.getDrops().clear();

        if(event.getEntity().getKiller() != null) {
            Player p = event.getEntity().getKiller();
            for (CustomItem item : itemManager.getCustomItems()) {
                if (item instanceof ActivateOnKill k) {
                    k.activateOnKill(p);
                }
            }
        }

        if(!(e instanceof Player)) {
            HashMap<LivingEntity, SpellDamage> fatalDamageMap = entityManager.getFatalDamageMap();
            fatalDamageMap.remove(e);

            CustomEntity ce = entityManager.getCustomEntityFromEntity(e);
            if(ce != null) {
                ce.tryDropLoot();
            }
        }
    }
    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        Player p = event.getPlayer();
        statusEffectManager.clearStatusEffects(p.getUniqueId());

        p.setVisualFire(TriState.FALSE);

        playerManager.getPlayerData(p).resetRightClick();
        itemManager.resetPlayerCharges(p);

        for(Attribute a : Registry.ATTRIBUTE) {
            AttributeInstance ai = p.getAttribute(a);
            if(ai == null) continue;
            for(AttributeModifier m : new ArrayList<>(ai.getModifiers()))
                ai.removeModifier(m);
        }

        HashMap<LivingEntity, SpellDamage> fatalDamageMap = entityManager.getFatalDamageMap();
        if(fatalDamageMap.containsKey(p))
            playerDeathDueToSpellDamage(event);
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event) {
        Player p = event.getPlayer();

        if(entityManager.isPlayerPuppet(p)) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Puppets cannot place blocks"));
            event.setCancelled(true);
            return;
        }
    }

    private void playerDeathDueToSpellDamage(PlayerDeathEvent event) {
        Player p = event.getPlayer();
        HashMap<LivingEntity, SpellDamage> fatalDamageMap = entityManager.getFatalDamageMap();

        if(!fatalDamageMap.containsKey(p)) {
            Bukkit.broadcast(Component.text("Player in fatalDamageMap not found"));
            return;
        }

        SpellDamage damage = fatalDamageMap.get(p);

        fatalDamageMap.remove(p);

        event.deathMessage(Component.text(damage.getDeathMessage()));
    }

    @EventHandler
    public void playerConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player p = event.getPlayer();
        ConsumableItem c = getItemManager().getCustomItemFromItem(item, ConsumableItem.class);

        if(c == null) return;

        c.onConsume(p, item);
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();

        if(e instanceof Mannequin m) {
            PuppetSpellEntity pse = entityManager.getPuppetFromMannequin(m);
            if(pse == null || !pse.entityExists()) return;

            Player p = pse.getOwner();
            pse.returnToOwner();
            p.damage(event.getDamage(), event.getDamageSource());
            return;
        }

        if(e instanceof Player p) {
            if(entityManager.isPlayerPuppet(p)) {
                PuppetSpellEntity pse = entityManager.getPuppetFromPlayer(p);
                if(pse == null || !pse.entityExists()) return;

                pse.damagePuppet(event.getFinalDamage());

                if(pse.getPuppetHP() <= 0) {
                    pse.returnToOwner();
                    event.setCancelled(true);

                }
                return;
            }

        }
    }

    @EventHandler
    public void entitySpawn(EntitySpawnEvent event) {
        Entity e = event.getEntity();

        spawnOverride(event);
    }

    private void spawnOverride(EntitySpawnEvent event) {
        Entity e = event.getEntity();
        if(e.getPersistentDataContainer().has(SpecialEntityKeys.spawnOverrideDisabled)) return;

        EntityType type = event.getEntityType();

        if(type == EntityType.ZOMBIE) {
            Random rand = new Random();
            int randInt = rand.nextInt(0,9);

            if(randInt == 0) {
                new ZombieApprentice().spawn(event.getLocation());
                event.setCancelled(true);
                return;
            }
        }
        if(type == EntityType.STRAY) {
            Random rand = new Random();
            int randInt = rand.nextInt(0,6);

            if(randInt == 0) {
                new IcicleThrower().spawn(event.getLocation());
                event.setCancelled(true);
                return;
            }
        }
    }
    //endregion

    public static final class SpecialEntityKeys {
        public static final NamespacedKey noExpDrops = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "no_exp_drops");
        public static final NamespacedKey removeOnDisable = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "remove_on_disable");
        public static final NamespacedKey spawnOverrideDisabled = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "spawn_override_disabled");
        public static final NamespacedKey noItemDrops = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "no_item_drops");

    }
}
