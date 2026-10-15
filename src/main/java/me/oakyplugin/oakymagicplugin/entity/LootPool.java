package me.oakyplugin.oakymagicplugin.entity;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.items.CustomItem;
import me.oakyplugin.oakymagicplugin.utility.BiPair;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class LootPool {

    private final ArrayList<LootPoolEntry> entries;
    private final int totalWeight;

    private LootPool(LootPoolBuilder builder) {
        entries = builder.entries;
        totalWeight = builder.totalWeight;
    }

    public LootPoolEntry getEntry(int index) {
        return entries.get(index);
    }

    public static LootPoolBuilder builder() {
        return new LootPoolBuilder();
    }

    public LootPoolEntry roll() {
        HashMap<LootPoolEntry, BiPair<Integer, Integer>> deathDropsIndex = new HashMap<>();
        int currentWeight = 0;

        for(LootPoolEntry x : entries) {
            deathDropsIndex.put(x, new BiPair<>(currentWeight, currentWeight + x.getWeight() - 1));
            currentWeight += x.getWeight();
        }

        Random rand = new Random();

        int randIndex = rand.nextInt(totalWeight);

        for(LootPoolEntry x : deathDropsIndex.keySet()) {
            BiPair<Integer, Integer> range = deathDropsIndex.get(x);

            if(randIndex >= range.left() && randIndex <= range.right())
                return x;
        }
        return null;
    }

    public static final class LootPoolBuilder {

        private ArrayList<LootPoolEntry> entries = new ArrayList<>();
        private int totalWeight;

        private LootPoolBuilder() {}

        public LootPoolBuilder addEntry(Class<? extends CustomItem> item, int weight, int count) {
            entries.add(new LootPoolEntry(item, weight, count));
            totalWeight += weight;
            return this;
        }
        public LootPool build() {
            return new LootPool(this);
        }
    }

    public static class LootPoolEntry {
        private final Class<? extends CustomItem> item;
        private final int weight;

        private final int count;

        private LootPoolEntry(Class<? extends CustomItem> item, int weight, int count) {
            this.item = item;
            this.weight = weight;
            this.count = count;
        }

        public Class<? extends CustomItem> getItemType() {
            return item;
        }

        public ItemStack getItemStack() {
            CustomItem cItem = OakyMagicPlugin.getPlugin().getItemManager().getCustomItem(item);
            return cItem.getItem(count);
        }

        public int getWeight() {
            return weight;
        }

        public int getCount() {
            return count;
        }
    }
}
