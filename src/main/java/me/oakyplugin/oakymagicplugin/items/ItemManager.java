package me.oakyplugin.oakymagicplugin.items;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.accessories.Accessory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;

public class ItemManager {
    private final Map<String, Usable> usableById = new HashMap<>();
    private final Map<Usable, Map<Player, Integer>> usableChargeTimes = new HashMap<>();

    private final Map<String, Accessory> accessoryById = new HashMap<>();

    private final Map<String, CustomItem> customItemById = new HashMap<>();
    private final Map<Class<? extends CustomItem>, CustomItem> customItemByClass = new HashMap<>();

    private final HashSet<TickableItem> tickableItems = new HashSet<>();

    public void tick() {
        for(TickableItem tickableItem : tickableItems) tickableItem.tick();
    }

    private void registerCustomItem(CustomItem item) {
        customItemById.put(item.getId(), item);
        customItemByClass.put(item.getClass(), item);
        if(item instanceof Usable usable) {
            usableById.put(usable.getId(), usable);
            usableChargeTimes.put(usable, new HashMap<>());
        }
        if(item instanceof Accessory acc) {
            accessoryById.put(acc.getId(), acc);
        }
        if(item instanceof TickableItem tickableItem)
            tickableItems.add(tickableItem);
    }
    public Usable getUsable(String id) {return usableById.get(id);}

    public Usable getUsableFromItem(ItemStack item) {
        if(item == null || item.getType().isAir()) return null;
        if(!item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        String usableId = meta.getPersistentDataContainer().get(
                Usable.itemIdKey,
                PersistentDataType.STRING
        );

        if(usableId == null) return null;

        return usableById.get(usableId);
    }

    public void chargeUsable(String usableId, Player p) {
        Usable usable = usableById.get(usableId);
        if(usable == null) return;

        Map<Player, Integer> playerMap = usableChargeTimes.get(usable);
        if(playerMap.containsKey(p))
            playerMap.replace(p, playerMap.get(p) + 1);
        else
            playerMap.put(p, 1);
    }
    public void resetUsableCharge(String usableId, Player p) {
        Usable usable = usableById.get(usableId);
        if(usable == null) return;

        Map<Player, Integer> playerMap = usableChargeTimes.get(usable);
        if(!playerMap.containsKey(p)) return;
        playerMap.remove(p);
    }
    public void setUsableCharge(String usableId, Player p, int charge) {
        Usable usable = usableById.get(usableId);
        if(usable == null) return;

        Map<Player, Integer> playerMap = usableChargeTimes.get(usable);
        if(playerMap.containsKey(p))
            playerMap.replace(p, charge);
        else
            playerMap.put(p, charge);
    }

    public int getUsableCharge(String usableId, Player p) {
        Usable usable = usableById.get(usableId);
        if(usable == null) return -1;

        Map<Player, Integer> playerMap = usableChargeTimes.get(usable);
        if(!playerMap.containsKey(p)) return -1;
        return playerMap.get(p);
    }
    public void resetPlayerCharges(Player p)
    {
        for(Usable usable : usableChargeTimes.keySet())
        {
            if(usableChargeTimes.get(usable) != null && usableChargeTimes.get(usable).get(p) != null && usableChargeTimes.get(usable).get(p) > 0) usable.onResetCharge(p);
            usableChargeTimes.get(usable).remove(p);
        }
    }

    public void registerAllItems() {
        Reflections reflections = new Reflections("me.oakyplugin.oakymagicplugin.items");

        Set<Class<? extends CustomItem>> classes =
                reflections.getSubTypesOf(CustomItem.class);

        for (Class<? extends CustomItem> clazz : classes) {

            // skip abstract classes
            if (Modifier.isAbstract(clazz.getModifiers())) continue;

            try {
                CustomItem item = clazz.getDeclaredConstructor().newInstance();
                registerCustomItem(item);
            } catch (Exception e) {
                System.out.println("Failed to load item: " + clazz.getName());
                e.printStackTrace();
            }
        }
    }

    public Accessory getAccessory(String id) {
        return accessoryById.get(id);
    }

    public Accessory getAccessoryFromItem(ItemStack item) {
        if(item == null || item.getType().isAir() || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        String id = meta.getPersistentDataContainer().get(OakyMagicPlugin.ITEM_ID, PersistentDataType.STRING);

        if(id == null) return null;

        return accessoryById.get(id);
    }

    public static boolean isAccessory(ItemStack item) {
        if(item == null || item.getType().isAir() || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        Boolean is = meta.getPersistentDataContainer().get(Accessory.accessoryKey, PersistentDataType.BOOLEAN);

        return is != null && is;
    }

    public <T extends CustomItem> boolean isCustomItemOfType(ItemStack item, Class<T> type) {
        if(item == null || item.getType().isAir() || !item.hasItemMeta()) return false;
        CustomItem customItem = getCustomItemFromItem(item);

        if(type == null || customItem == null) return false;
        return type.isInstance(customItem);
    }

    public CustomItem getCustomItemByName(String id) {
        return customItemById.get(id);
    }
    public <T extends CustomItem> T getCustomItem(Class<T> clazz) {
        CustomItem item = customItemByClass.get(clazz);

        if(item == null) return null;

        if(clazz.isInstance(item))
            return clazz.cast(item);
        return null;
    }
    public CustomItem getCustomItemFromItem(ItemStack item) {
        if(item == null || item.getType().isAir() || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        String id = meta.getPersistentDataContainer().get(OakyMagicPlugin.ITEM_ID, PersistentDataType.STRING);

        if(id == null) return null;

        return customItemById.get(id);
    }
    public <T extends CustomItem> T getCustomItemFromItem(ItemStack item, Class<T> type) {
        if(item == null || item.getType().isAir() || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        String id = meta.getPersistentDataContainer().get(OakyMagicPlugin.ITEM_ID, PersistentDataType.STRING);

        if(id == null) return null;

        CustomItem customItem = customItemById.get(id);

        if(type.isInstance(customItem))
            return (type.cast(customItem));
        return null;
    }
    public CustomItem[] getCustomItems() {
        CustomItem[] items = new CustomItem[customItemById.size()];

        int i = 0;
        for(CustomItem item : customItemById.values()) {
            items[i] = item;
            i++;
        }
        return items;
    }
}
